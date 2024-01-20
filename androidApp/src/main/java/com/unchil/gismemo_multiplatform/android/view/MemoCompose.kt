package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import androidx.biometric.BiometricManager
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.size.Size
import coil3.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.common.biometricPrompt
import com.unchil.gismemo_multiplatform.android.common.launchIntent_Biometric_Enroll
import com.unchil.gismemo_multiplatform.android.common.launchIntent_ShareMemo
import com.unchil.gismemo_multiplatform.android.model.BiometricCheckObject
import com.unchil.gismemo_multiplatform.android.model.MemoBgObject
import com.unchil.gismemo_multiplatform.android.model.SnackBarChannelObject
import com.unchil.gismemo_multiplatform.android.navigation.GisMemoDestinations
import com.unchil.gismemo_multiplatform.android.theme.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.viewModel.MemoListViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch


val SwipeBoxHeight = 70.dp

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MemoCompose(
    item: MEMO_TBL,
    channel:  Channel<Int>? = null,
    event: ((MemoListViewModel.Event)->Unit)? = null,
    navController: NavHostController? = null
){

    val context = LocalContext.current
    val repository = LocalRepository.current
    val coroutineScope = rememberCoroutineScope()
/*
    val isUsableHaptic = LocalUsableHaptic.current
    val hapticFeedback = LocalHapticFeedback.current


    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

 */


    val permissions = listOf(
        Manifest.permission.USE_BIOMETRIC,
    )
    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)


    var isGranted by remember { mutableStateOf(true) }
    permissions.forEach { chkPermission ->
        isGranted =  isGranted && multiplePermissionsState.permissions.find {
            it.permission == chkPermission
        }?.status?.isGranted ?: false
    }


    val onResult: (isSucceeded:Boolean, type: BiometricCheckObject.Type, errorMsg:String? ) -> Unit =
        { isSucceeded, type, _ ->
            if(isSucceeded){
                when(type){
                    BiometricCheckObject.Type.DETAILVIEW -> {
                        navController?.let {
                            if (event != null) {
                                event(
                                    MemoListViewModel.Event.ToRoute(
                                        navController = it,
                                        route = GisMemoDestinations.DetailMemo.createRoute(id = item.id)
                                    )
                                )
                            }
                        }
                    }
                    BiometricCheckObject.Type.SHARE -> {
                        coroutineScope.launch {
                            launchIntent_ShareMemo(context = context, memo = item, repository = repository)
                        }
                    }
                    BiometricCheckObject.Type.DELETE -> {
                        if (event != null) {
                            event(  MemoListViewModel.Event.DeleteItem(id = item.id) )
                        }
                        channel?.let {channel ->
                            channel.trySend( SnackBarChannelObject.entries.first {item ->
                                item.channelType == SnackBarChannelObject.Type.MEMO_DELETE
                            }.channel)
                        }
                    }
                }
            }else {
                channel?.let {channel ->
                    channel.trySend(
                        SnackBarChannelObject.entries.first {item ->
                            item.channelType == SnackBarChannelObject.Type.AUTHENTICATION_FAILED
                        }.channel
                    )
                }

            }
        }


    val anchorOffset = 80.dp
    val isAnchor = remember { mutableStateOf(false) }
    val isToStart = remember { mutableStateOf(false) }

    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.Default -> {
                    isAnchor.value = false
                    false
                }
                DismissValue.DismissedToEnd -> {
                    isToStart.value = false
                    isAnchor.value = !isAnchor.value
                    false
                }
                DismissValue.DismissedToStart -> {
                    isToStart.value = true
                    isAnchor.value = !isAnchor.value
                    false
                }
            }
        }
    )

    val dismissContentOffset by  remember {
        mutableStateOf(
            if (isAnchor.value) {
                if (isToStart.value) - anchorOffset else anchorOffset
            } else {
                0.dp
            }
        )
    }

    val checkBiometricSupport: (() -> Unit) = {
        val biometricManager = BiometricManager.from(context)
        when (  biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
                    or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> { }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                launchIntent_Biometric_Enroll(context)
            }
            else -> {
                channel?.trySend(SnackBarChannelObject.entries.first {item ->
                    item.channelType == SnackBarChannelObject.Type.BIOMETRIC_NO_SUCCESS
                }.channel)
            }
        }

    }

    Card(
        modifier = Modifier
            .height(260.dp)
            .padding(top = 2.dp) ,
        shape = ShapeDefaults.ExtraSmall ,
        onClick = {
         //   hapticProcessing()
            if(item.isSecret && isGranted ) {
                checkBiometricSupport.invoke()
                biometricPrompt(context, BiometricCheckObject.Type.DETAILVIEW, onResult)
            }else {
                navController?.let {
                    if (event != null) {
                        event(
                            MemoListViewModel.Event.ToRoute(
                                navController = it,
                                route = "detailmemo?${item.id}"
                            )
                        )
                    }
                }
            }

        }

    ) {

        SwipeToDismiss(
            modifier = Modifier
            ,
            state = dismissState,
            background = {
                BackgroundContent(dismissState) {
                    when(it){
                        MemoBgObject.Type.SHARE -> {

                            if(item.isSecret && isGranted ) {
                                checkBiometricSupport.invoke()
                                biometricPrompt(context, BiometricCheckObject.Type.SHARE, onResult)
                            }else {
                                coroutineScope.launch {
                                    launchIntent_ShareMemo(context = context, memo = item, repository = repository )
                                }
                            }

                        }
                        MemoBgObject.Type.DELETE -> {

                            if(item.isSecret && isGranted ) {
                                checkBiometricSupport.invoke()
                                biometricPrompt(context, BiometricCheckObject.Type.DELETE, onResult)
                            }else {
                                if (event != null) {
                                    event(  MemoListViewModel.Event.DeleteItem(id = item.id) )
                                }
                                channel?.let {channel ->
                                    channel.trySend(SnackBarChannelObject.entries.first {item ->
                                        item.channelType == SnackBarChannelObject.Type.MEMO_DELETE
                                    }.channel)
                                }
                            }
                        }
                    }

                    isAnchor.value = false
                }
            },
            dismissContent = {

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SwipeBoxHeight)
                        .offset(x = dismissContentOffset)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                    ,

                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable(false, null, null) {}
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {


                        Icon(
                            modifier = Modifier.scale(1f),
                            imageVector = if (item.isSecret) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                            contentDescription = "Lock",
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.8f),
                            verticalArrangement = Arrangement.SpaceAround,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text =  item.title,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                minLines =  1,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text =   item.desc,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                minLines =  1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text =   item.snippets,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                minLines =  1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Icon(
                            modifier = Modifier.scale(1f),
                            imageVector = if (item.isPin) Icons.Outlined.LocationOn else Icons.Outlined.LocationOff,
                            contentDescription = "Mark",
                        )


                    }
                }

            }
        )

        ImageViewer(data = item.snapshot, size = Size.ORIGINAL, isZoomable = false)
// Testing
//        ImageViewer(data = item.snapshot.toUri(), size = Size.ORIGINAL, isZoomable = false)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackgroundContent(
    dismissState: DismissState,
    onClick:(MemoBgObject.Type)->Unit
){

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.surface
            DismissValue.DismissedToEnd -> Color.Blue.copy(alpha = 0.3f)
            DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.3f)
        }, label = ""
    )
    val scale by animateFloatAsState(
        when (dismissState.targetValue == DismissValue.Default) {
            true -> 1f
            else -> 1.3f
        }, label = ""
    )


/*
    val isUsableHaptic = LocalUsableHaptic.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }


 */


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SwipeBoxHeight)
            .background(color)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.CenterStart
        ) {

            Row {

                IconButton(
                    onClick = {
                   //     hapticProcessing()
                        onClick(MemoBgObject.Type.SHARE)
                    }
                ) {
                    Icon(
                        modifier = Modifier.scale(scale),
                        imageVector = MemoBgObject.desc(MemoBgObject.Type.SHARE).second,
                        contentDescription = MemoBgObject.desc(MemoBgObject.Type.SHARE).first
                    )
                }
            }
        }


        Box(
            modifier = Modifier,
            contentAlignment = Alignment.CenterEnd
        ) {

            Row {
                IconButton(
                    onClick = {
                     //   hapticProcessing()
                        onClick(MemoBgObject.Type.DELETE)
                    }
                ) {
                    Icon(
                        modifier = Modifier.scale(scale),
                        imageVector =  MemoBgObject.desc(MemoBgObject.Type.DELETE).second,
                        contentDescription = MemoBgObject.desc(MemoBgObject.Type.DELETE).first
                    )
                }
            }
        }

    }

}



@Preview
@Composable
fun PrevMemoCompose() {
    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val repository = PlatformObject.getRepository(context)
    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {
        MyApplicationTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val snapshot =
                    Uri.parse("android.resource://com.unchil.gismemo_multiplatform.android/" + R.drawable.snapshot)
                        .toString()

                Box(
                    modifier = Modifier
                ) {
                    MemoCompose(
                        item = MEMO_TBL(
                            id = 0, latitude = 0f, longitude = 0f, altitude = 0f, isSecret = true, isPin = false,
                            title = "2024-01-17 12:46", snippets = "#FitnessCenter #FlightLand #FlightTakeoff",
                            desc = "screenshot:1 audioText:0 photo:0 video:0", snapshot = snapshot ,
                            snapshotCnt = 1, textCnt = 0, photoCnt = 0, videoCnt = 0
                        )
                    )
                }


            }
        }
    }

}
