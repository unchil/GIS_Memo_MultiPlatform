package com.unchil.gismemo_multiplatform.android.view


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.LocalChangeLocale
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.LocalUsableDarkMode
import com.unchil.gismemo_multiplatform.android.LocalUsableDynamicColor
import com.unchil.gismemo_multiplatform.android.LocalUsableHaptic
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.common.getLanguageArray
import com.unchil.gismemo_multiplatform.android.model.SnackBarChannelObject
import com.unchil.gismemo_multiplatform.android.theme.GisMemoTheme
import com.unchil.gismemo_multiplatform.android.viewModel.SettingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Locale


fun hapticProcessing(
    coroutineScope: CoroutineScope,
    hapticFeedback: HapticFeedback,
    isUsableHaptic:Boolean
){
    if(isUsableHaptic) {
        coroutineScope.launch {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
}

@Composable
fun SettingScreen(navController:NavHostController){
// LocalChangeLocale.current 값 호출로 locale 실시간 반영
    val localeChange = LocalChangeLocale.current

    val context = LocalContext.current
    val languageArray = context.getLanguageArray()


    val hapticFeedback = LocalHapticFeedback.current

    val isUsableHaptic = LocalUsableHaptic.current
    val isUsableDarkMode = LocalUsableDarkMode.current
    val isUsableDynamicColor= LocalUsableDynamicColor.current
    val repository = LocalRepository.current
    val viewModel = remember {  SettingViewModel(repository = repository ) }


    var isLocaleChange by rememberSaveable { mutableStateOf(false) }
    var checkedIsUsableHaptic by remember { mutableStateOf(isUsableHaptic) }
    var checkedIsDarkMode by remember { mutableStateOf(isUsableDarkMode) }
    var checkedIsDynamicColor by remember { mutableStateOf(isUsableDynamicColor) }

    val coroutineScope = rememberCoroutineScope()
    val isAlertDialog = rememberSaveable { mutableStateOf(false) }


    val iconIsUsableHaptic: (@Composable () -> Unit)? = if (checkedIsUsableHaptic) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else { null }

    val iconIsDarkMode: (@Composable () -> Unit)? = if (checkedIsDarkMode) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else { null }

    val iconIsDynamicColor: (@Composable () -> Unit)? = if (checkedIsDynamicColor) {
        {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else { null }


    val localeOption =  listOf(
        context.resources.getString(R.string.setting_Locale_ko),
        context.resources.getString(R.string.setting_Locale_en),
        context.resources.getString(R.string.setting_Locale_fr),
        context.resources.getString(R.string.setting_Locale_zh),
        context.resources.getString(R.string.setting_Locale_pt),
        context.resources.getString(R.string.setting_Locale_es)
    )



    val localeRadioGroupState = remember {
        mutableStateOf( viewModel.repository.isChangeLocale.value )
    }

    LaunchedEffect(key1 = localeRadioGroupState.value ){

        val locale = Locale( languageArray[localeRadioGroupState.value] )

        Locale.setDefault(locale)

        context.resources.configuration.setLocale(locale)
        context.resources.configuration.setLayoutDirection(locale)
        context.resources.updateConfiguration( context.resources.configuration, context.resources.displayMetrics)

        viewModel.onEvent(SettingViewModel.Event.UpdateIsChangeLocale(localeRadioGroupState.value))

        // locale change 실시간 반영을 위한 dummy event 발생
        isLocaleChange = !isLocaleChange
        viewModel.onEvent(SettingViewModel.Event.UpdateOnChangeLocale(isLocaleChange))


    }


    val snackBarHostState = remember { SnackbarHostState() }
    val channel = remember { Channel<Int>(Channel.CONFLATED) }

    LaunchedEffect(channel) {
        channel.receiveAsFlow().collect { index ->
            val channelData = SnackBarChannelObject.entries.first { item ->
                item.channel == index
            }

            val result = snackBarHostState.showSnackbar(
                message =   context.resources.getString( channelData.message),
                actionLabel = channelData.actionLabel,
                withDismissAction = channelData.withDismissAction,
                duration = channelData.duration
            )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                    //----------
                    when (channelData.channelType) {

                        else -> {}
                    }
                    //----------
                }
                SnackbarResult.Dismissed -> {
                    hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                }
            }
        }
    }



    Scaffold(
        snackbarHost ={
            SnackbarHost(hostState = snackBarHostState)
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {



        Box(modifier = Modifier.fillMaxSize().padding(it)) {

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(500.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Divider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = context.resources.getString(R.string.setting_UsableHaptic),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Switch(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .semantics { contentDescription = "IS Usable Haptic " },
                        checked = checkedIsUsableHaptic,
                        onCheckedChange = {
                            hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                            checkedIsUsableHaptic = it
                            viewModel.onEvent(SettingViewModel.Event.UpdateIsUsableHaptic(it))
                        },
                        thumbContent = iconIsUsableHaptic
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = context.resources.getString(R.string.setting_UsableDarkMode),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Switch(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .semantics {
                                contentDescription = "IS Usable DarkMode "
                            },
                        checked = checkedIsDarkMode,
                        onCheckedChange = {
                            hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                            checkedIsDarkMode = it
                            viewModel.onEvent(SettingViewModel.Event.UpdateIsUsableDarkMode(it))
                        },
                        thumbContent = iconIsDarkMode
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = context.resources.getString(R.string.setting_UsableDynamicColor),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Switch(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .semantics {
                                contentDescription = "IS Usable DynamicColor "
                            },
                        checked = checkedIsDynamicColor,
                        onCheckedChange = {
                            hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                            checkedIsDynamicColor = it
                            viewModel.onEvent(SettingViewModel.Event.UpdateIsDynamicColor(it))
                        },
                        thumbContent = iconIsDynamicColor
                    )
                }



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = context.resources.getString(R.string.setting_ClearAllMemo),
                        style = MaterialTheme.typography.titleSmall
                    )

                    IconButton(
                        modifier = Modifier
                            .scale(1.2f)
                            .fillMaxWidth(0.5f),
                        onClick = {
                            hapticProcessing(coroutineScope, hapticFeedback, isUsableHaptic)
                            isAlertDialog.value = true

                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Clear All Memo"
                            )
                        }
                    )
                }



                Divider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Language,
                        contentDescription = "locale"
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                    Text(text = context.resources.getString(R.string.setting_Locale),
                        style = MaterialTheme.typography.titleSmall)
                }


                RadioButtonGroupCompose(
                    state = localeRadioGroupState,
                    data = localeOption,
                    layoutScopeType = "Column"
                )


                Divider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp))


            }


            if( isAlertDialog.value) {
                DeleteConfirmDialog(isAlertDialog){
                    viewModel.onEvent(SettingViewModel.Event.clearAllMemo)

                    channel.trySend(SnackBarChannelObject.entries.first {item ->
                        item.channelType == SnackBarChannelObject.Type.ALL_DATA_DELETE
                    }.channel)
                }

            }

        }
    }



}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmDialog(
    isAlertDialog: MutableState<Boolean>,
    event: (()-> Unit)? = null
){

    val context = LocalContext.current
    val isUsableHaptic = LocalUsableHaptic.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    fun hapticProcessing() {
        if (isUsableHaptic) {
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            isAlertDialog.value = false
        }
    ) {


        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        6.dp
                    ),
                    shape = ShapeDefaults.ExtraSmall
                )
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {


            androidx.compose.material.Text(
                modifier = Modifier,
                text = context.resources.getString(R.string.setting_DeleteAlertDialog_Title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Red
            )


            TextButton(

                onClick = {
                    hapticProcessing()
                    isAlertDialog.value = false
                    //event(SettingsViewModel.Event.clearAllMemo)
                    event?.invoke()
                }
            ) {
                androidx.compose.material.Text(
                    context.resources.getString(R.string.setting_DeleteAlertDialog_Confirm),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Red
                )
            }

        }



    }


}




@Preview
@Composable
fun PrevSettingsView(){
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = PlatformObject.getRepository(context)
    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {
        GisMemoTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = androidx.compose.material.MaterialTheme.colors.onPrimary,
                contentColor = androidx.compose.material.MaterialTheme.colors.primary
            ) {
                SettingScreen(navController = navController)
            }
        }

    }


}


