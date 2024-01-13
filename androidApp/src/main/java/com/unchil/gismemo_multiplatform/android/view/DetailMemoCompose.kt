package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.BedtimeOff
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ModeOfTravel
import androidx.compose.material.icons.outlined.OpenWith
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.widgets.ScaleBar
import com.unchil.gismemo.shared.composables.LocalPermissionsManager
import com.unchil.gismemo.shared.composables.PermissionsManager
import com.unchil.gismemo.view.WeatherView
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName
import com.unchil.gismemo_multiplatform.android.model.MapTypeMenuData

import com.unchil.gismemo_multiplatform.android.model.SettingMenuData
import com.unchil.gismemo_multiplatform.android.model.SnackBarChannelObject
import com.unchil.gismemo_multiplatform.android.model.TagInfoDataObject
import com.unchil.gismemo_multiplatform.android.theme.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.viewModel.DetailMemoViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState", "MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    MapsComposeExperimentalApi::class
)
@Composable
fun DetailMemoCompose(navController: NavController, id:Long) {

    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)
    var isGranted by remember { mutableStateOf(true) }
    permissions.forEach { chkPermission ->
        isGranted =  isGranted && multiplePermissionsState.permissions.find {
            it.permission == chkPermission }?.status?.isGranted ?: false
    }
    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions ,
        viewType = PermissionRequiredComposeFuncName.MemoMap
    ) {
        val context = LocalContext.current
        val repository = LocalRepository.current
        val density = LocalDensity.current

        val viewModel = remember { DetailMemoViewModel(repository = repository)}

        val memoID by rememberSaveable { mutableStateOf(id) }

        LaunchedEffect(key1 = memoID) {
            viewModel.onEvent(DetailMemoViewModel.Event.SetDetailMemo(id = id))
        }

        val memo = viewModel.memo.collectAsState()

        val selectedTagArray = viewModel.tagArrayList.collectAsState()
        val selectedTags =  mutableStateOf(selectedTagArray.value)

        val weatherData = viewModel.weather.collectAsState()

        val coroutineScope = rememberCoroutineScope()
        /*
        val isUsableHaptic = LocalUsableHaptic.current
        val hapticFeedback = LocalHapticFeedback.current
        fun hapticProcessing() {
            if (isUsableHaptic) {
                coroutineScope.launch {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        }
        val isUsableDarkMode = LocalUsableDarkMode.current
        var isDarkMode by remember{ mutableStateOf(isUsableDarkMode) }
         */

        var mapTypeIndex by rememberSaveable { mutableIntStateOf(0) }
        var mapProperties by remember {
            mutableStateOf(
                MapProperties(
                    mapType =    MapType.entries.first { mapType ->
                        mapType.name == MapTypeMenuData.Types[mapTypeIndex].name
                    },
                    isMyLocationEnabled = true,
                    /*
                    mapStyleOptions = if(isDarkMode) {
                            MapStyleOptions.loadRawResourceStyle(
                                context,
                                R.raw.mapstyle_night
                            )
                        } else { null }
                     */
                )
            )
        }

        val uiSettings by remember {
            mutableStateOf( MapUiSettings(zoomControlsEnabled = false) )
        }
        val sheetState = SheetState(
            skipPartiallyExpanded = false,
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
        val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
        var isTagDialog by rememberSaveable { mutableStateOf(false) }
        val isVisibleMenu = rememberSaveable { mutableStateOf(false) }
        val isLock = mutableStateOf(memo.value?.isSecret ?: false)
        val isMark = mutableStateOf(memo.value?.isPin ?: false)
        val snippets = mutableStateOf(memo.value?.snippets ?: "")

        var isTitleBox by  rememberSaveable{  mutableStateOf(true)}

        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

        val currentLocation =  mutableStateOf(
            LatLng(
                memo.value?.latitude?.toDouble() ?: 0.0,
                memo.value?.longitude?.toDouble() ?: 0.0
            )
        )

        var isGoCurrentLocation by remember { mutableStateOf(false) }
        val markerState = MarkerState(position = currentLocation.value)
        val defaultCameraPosition = CameraPosition.fromLatLngZoom(currentLocation.value, 16f)
        val cameraPositionState = CameraPositionState(position = defaultCameraPosition)

        val snackbarHostState = remember { SnackbarHostState() }
        val channel = remember { Channel<Int>(Channel.CONFLATED) }
        LaunchedEffect(channel) {

            channel.receiveAsFlow().collect { index ->
                val channelData = SnackBarChannelObject.entries.first {channelInfo ->
                    channelInfo.channel == index
                }

                val result = snackbarHostState.showSnackbar(
                    message = context.resources.getString( channelData.message),
                    actionLabel = channelData.actionLabel,
                    withDismissAction = channelData.withDismissAction,
                    duration = channelData.duration
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                  //      hapticProcessing()
                        when (channelData.channelType) {
                            else -> {}
                        }
                    }
                    SnackbarResult.Dismissed -> {
                   //     hapticProcessing()
                    }
                }
            }
        }


        val tagDialogDissmissHandler:() -> Unit = {
           // hapticProcessing()
            selectedTags.value.clear()
            var snippetsTemp = ""
            TagInfoDataObject.entries.forEachIndexed { index, tagInfoData ->
                if (tagInfoData.isSet.value) {
                    snippetsTemp = "${snippetsTemp } #${  context.resources.getString( 
                      //  TagInfoDataList[index].name
                        tagInfoData.name
                    )}"
                    selectedTags.value.add(index)
                }
            }
            snippets.value = snippetsTemp
            viewModel.onEvent(
                DetailMemoViewModel.Event.UpdateTagList(
                    id,
                    selectedTags.value,
                    snippets.value
                )
            )
        }

        val checkEnableLocationService: () -> Unit = {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(context.mainExecutor) { task ->
                if (!task.isSuccessful || task.result == null) {
                    channel.trySend(SnackBarChannelObject.entries.first {channelInfo ->
                        channelInfo.channelType == SnackBarChannelObject.Type.LOCATION_SERVICE_DISABLE
                    }.channel)
                }
            }
        }

        @Composable
        fun SheetDragHandler() {
            Box(
                modifier = Modifier.height(30.dp),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    modifier = Modifier
                        .scale(1f)
                        .clickable {
                            coroutineScope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden
                                    || scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
                                ) {
                                    scaffoldState.bottomSheetState.expand()
                                } else {
                                    scaffoldState.bottomSheetState.hide()
                                }
                            }
                        },
                    imageVector = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)
                        Icons.Outlined.KeyboardArrowDown
                            else
                        Icons.Outlined.KeyboardArrowUp,
                    contentDescription = "search",
                )
            }
        }

        BottomSheetScaffold(
            modifier = Modifier.statusBarsPadding(),
            scaffoldState = scaffoldState,
            sheetContainerColor = MaterialTheme.colorScheme.surface,
            sheetContentColor = MaterialTheme.colorScheme.onSurface,
            sheetContent = {
                MemoDataCompose()
            },
            sheetDragHandle = { SheetDragHandler() },
            sheetPeekHeight = 90.dp,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings,
                    onMapLongClick = {},
                    onMapClick = {
                        if(isTagDialog)  {
                            tagDialogDissmissHandler()
                            isTagDialog = false
                        }
                    },
                    onMyLocationButtonClick = {
                        checkEnableLocationService.invoke()
                        return@GoogleMap false
                    }
                ) {
                    MapEffect(key1 = isGoCurrentLocation) {
                        if (isGoCurrentLocation) {
                            it.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation.value, 16F))
                            isGoCurrentLocation = false
                        }
                    }
                    Marker(
                        state = markerState,
                        title = "lat/lng:(${
                            String.format(
                                "%.5f",
                                markerState.position.latitude
                            )
                        },${String.format("%.5f", markerState.position.longitude)})",
                    )
                }

                memo.value?.let {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(500.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        AnimatedVisibility(visible = isTitleBox,
                            enter = slideInVertically {
                                // Slide in from 40 dp from the top.
                                with(density) { 40.dp.roundToPx() }
                            } + expandVertically(
                                // Expand from the top.
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                // Fade in with the initial alpha of 0.3f.
                                initialAlpha = 0.3f
                            ),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut()
                        ) {


                            Column(
                                modifier = Modifier
                                    .padding(all = 20.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                                        shape = ShapeDefaults.ExtraSmall
                                    ),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                weatherData.value?.let {
                                    WeatherView(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    6.dp
                                                ),
                                                shape = ShapeDefaults.ExtraSmall
                                            ),
                                        item = it.toCURRENTWEATHER_TBL()
                                    )
                                }

                                Divider(modifier = Modifier.padding(bottom = 10.dp))
                                Text(text = it.title, style = MaterialTheme.typography.titleSmall)
                                Text(text = it.desc, style = MaterialTheme.typography.bodySmall)
                                Text(text = snippets.value, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.padding(bottom = 10.dp))

                            }
                        }

                    }
                }

                ScaleBar(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 50.dp, end = 10.dp),
                    cameraPositionState = cameraPositionState
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            shape = ShapeDefaults.ExtraSmall
                        )
                ) {

                    AnimatedVisibility(
                        visible = isVisibleMenu.value,
                    ) {

                        IconButton(
                            onClick = {
                             //   hapticProcessing()
                                isGoCurrentLocation = true
                            }
                        ) {
                            Icon(
                                modifier = Modifier.scale(1f),
                                imageVector = Icons.Outlined.ModeOfTravel,
                                contentDescription = "ModeOfTravel",
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = isVisibleMenu.value,
                    ) {

                        IconButton(
                            enabled = if(mapTypeIndex == 0) true else false,
                            onClick = {
                                /*
                                hapticProcessing()
                                isDarkMode = !isDarkMode

                                if (isDarkMode) {
                                    mapProperties = mapProperties.copy(
                                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                                            context,
                                            R.raw.mapstyle_night
                                        )
                                    )
                                } else {
                                    mapProperties = mapProperties.copy(mapStyleOptions = null)
                                }

                                 */
                            }
                        ) {
                            Icon(
                                modifier = Modifier.scale(1f),
                                imageVector = if (false)
                               // imageVector = if (isDarkMode)
                                    Icons.Outlined.BedtimeOff
                                    else
                                    Icons.Outlined.DarkMode,
                                contentDescription = "DarkMode",
                            )
                        }
                    }

                }


                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            shape = ShapeDefaults.ExtraSmall
                        )
                ) {
                    MapTypeMenuData.Types.forEachIndexed { index, it ->
                        AnimatedVisibility(
                            visible = isVisibleMenu.value,
                        ) {
                            IconButton(
                                onClick = {
                                  //  hapticProcessing()
                                    val mapType = MapType.entries.first { mapType ->
                                        mapType.name == it.name
                                    }
                                    mapProperties = mapProperties.copy(mapType = mapType)
                                    mapTypeIndex = index

                                }) {

                                Icon(
                                    imageVector = MapTypeMenuData.desc(it).first,
                                    contentDescription = it.name,
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            shape = ShapeDefaults.ExtraSmall
                        )
                ) {

                    IconButton(
                        onClick = {
                         //   hapticProcessing()
                            isVisibleMenu.value = !isVisibleMenu.value
                            isTitleBox = !isTitleBox
                        }
                    ) {
                        Icon(
                            modifier = Modifier.scale(1f),
                            imageVector = if (isVisibleMenu.value)
                                    Icons.Outlined.OpenWith
                                else
                                    Icons.Outlined.Api,
                            contentDescription = "Menu",
                        )
                    }

                    SettingMenuData.Types.forEach {menutype ->
                        AnimatedVisibility(
                            visible = isVisibleMenu.value,
                        ) {
                            IconButton(
                                onClick = {
                                //    hapticProcessing()
                                    when (menutype) {
                                        SettingMenuData.Type.SECRET -> {
                                            isLock.value = !isLock.value
                                            memo.value?.let {
                                                viewModel.onEvent(
                                                    DetailMemoViewModel.Event.UpdateIsSecret(
                                                        id = it.id,
                                                        isSecret = isLock.value
                                                    )
                                                )
                                            }

                                            val snackBarChannelType =
                                                if (isLock.value)
                                                    SnackBarChannelObject.Type.LOCK_CHANGE_SET
                                                else
                                                    SnackBarChannelObject.Type.LOCK_CHANGE_FREE

                                            channel.trySend(SnackBarChannelObject.entries.first {channelInfo ->
                                                channelInfo.channelType == snackBarChannelType
                                            }.channel)
                                        }

                                        SettingMenuData.Type.MARKER -> {
                                            isMark.value = !isMark.value
                                            memo.value?.let {
                                                viewModel.onEvent(
                                                    DetailMemoViewModel.Event.UpdateIsMark(
                                                        id = it.id,
                                                        isMark = isMark.value
                                                    )
                                                )
                                            }

                                            val snackBarChannelType =
                                                if (isMark.value)
                                                    SnackBarChannelObject.Type.MARKER_CHANGE_SET
                                                else
                                                    SnackBarChannelObject.Type.MARKER_CHANGE_FREE

                                            channel.trySend(SnackBarChannelObject.entries.first {channelInfo ->
                                                channelInfo.channelType == snackBarChannelType
                                            }.channel)

                                        }
                                        SettingMenuData.Type.TAG -> {
                                            isTagDialog = !isTagDialog
                                            if(!isTagDialog){
                                                tagDialogDissmissHandler.invoke()
                                            }
                                        }

                                    }
                                }) {
                                val icon = when (menutype) {
                                    SettingMenuData.Type.SECRET -> {
                                        if (isLock.value)
                                            SettingMenuData.desc(menutype).first
                                        else
                                            SettingMenuData.desc(menutype).second
                                                ?:SettingMenuData.desc(menutype).first
                                    }
                                    SettingMenuData.Type.MARKER -> {
                                        if (isMark.value) SettingMenuData.desc(menutype).first
                                        else SettingMenuData.desc(menutype).second
                                            ?: SettingMenuData.desc(menutype).first
                                    }
                                    else -> {
                                        SettingMenuData.desc(menutype).first
                                    }

                                }

                                Icon(
                                    imageVector = icon,
                                    contentDescription = menutype.name,
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden
                                    || scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
                                ) {
                                    scaffoldState.bottomSheetState.expand()
                                } else {
                                    scaffoldState.bottomSheetState.hide()
                                }
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = "Data Container"
                        )
                    }
                }


                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                            shape = ShapeDefaults.ExtraSmall
                        )
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {


                    AnimatedVisibility(visible = isTagDialog) {
                        AssistChipGroupView(
                            isVisible = isTagDialog,
                            setState = selectedTags,
                        )
                    }

                }

            }





        }
    }
}

@Preview
@Composable
fun PrevDetailMemo(){
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val context = LocalContext.current

   // val repository = PlatformHandler.Builder(context).repository
    val repository = PlatformObject.getRepository(context)

    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {

        MyApplicationTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = androidx.compose.material.MaterialTheme.colors.onPrimary,
                contentColor = androidx.compose.material.MaterialTheme.colors.primary
            ) {
                DetailMemoCompose(navController = navController, id = 1705031322810L)
            }
        }

    }




}
