package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.rounded.HighlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.theme.GisMemoTheme
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.FileManager
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName
import com.unchil.gismemo_multiplatform.android.viewModel.SpeechRecognizerViewModel
import java.io.FileOutputStream
import java.util.Locale

val recognizerIntent =  {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
    )

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE,
        Locale.getDefault().language
    )

    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk")

    /*
    intent.putExtra("android.speech.extra.GET_AUDIO", true)
    intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")
     */
}


@SuppressLint("UnrememberedMutableState", "Recycle")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SpeechRecognizerCompose(navController: NavHostController) {

    val coroutineScope = rememberCoroutineScope()

    val permissions =  remember { listOf(Manifest.permission.RECORD_AUDIO) }
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    val context = LocalContext.current
    val repository = LocalRepository.current
    val viewModel = remember { SpeechRecognizerViewModel( repository = repository ) }

    val currentBackStack by navController.currentBackStackEntryAsState()

    var recordingUrl: String?  by rememberSaveable { mutableStateOf(null) }


    val currentAudioTextList = viewModel._currentAudioText

    val audioTextList:MutableList<Pair<String, List<String>>>
            =  rememberSaveable { currentAudioTextList }


    val audioTextData:Pair<MutableState<String>, MutableList<String>>
            =  rememberSaveable { Pair(mutableStateOf(""), mutableListOf()) }


    val startLauncherRecognizerIntent = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()  ) {

        if(it.resultCode == Activity.RESULT_OK){

            val result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            audioTextData.first.value = audioTextData.first.value + result?.get(0).toString() + " "

            it.data?.data?.let {uri ->


                FileManager.getFilePath(context, FileManager.Companion.OUTPUTFILE.AUDIO).let{ outputFilePath ->
                    context.contentResolver.openInputStream(uri)?.copyTo(FileOutputStream(outputFilePath))

                    audioTextData.second.add(
                        outputFilePath
                    )

                    recordingUrl = outputFilePath

                }


            }
        }
    }


    val  recognizerIntent =  remember {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().language )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk")

        intent.putExtra("android.speech.extra.GET_AUDIO", true)
        intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR")

    }

    val scrollState = rememberScrollState()

    val backStack = {
        if (audioTextData.first.value.length > 0) {
            audioTextList.add(
                Pair(
                    audioTextData.first.value,
                    audioTextData.second.toList()
                )
            )

            viewModel.onEvent(
                SpeechRecognizerViewModel.Event.SetAudioText(data = audioTextList)
            )
        }
        navController.popBackStack()
    }


    var isGranted by mutableStateOf(true)
    permissions.forEach { chkPermission ->
        isGranted =  isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission }?.status?.isGranted
                ?: false
    }



    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions,
        viewType = PermissionRequiredComposeFuncName.SpeechToText
    ) {

        Column(modifier = Modifier
            .verticalScroll(state = scrollState)
            .fillMaxWidth()
            .padding(20.dp),
            horizontalAlignment= Alignment.CenterHorizontally
        ) {


            OutlinedTextField(
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth(),
                singleLine = false,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            audioTextData.first.value  = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.HighlightOff,
                            contentDescription = "Clear"
                        )
                    }
                },
                value = audioTextData.first.value ,
                onValueChange = { audioTextData.first.value  = it },
                label = { Text("Speech To Text") },
                shape = OutlinedTextFieldDefaults.shape,
                keyboardActions = KeyboardActions.Default
            )


            Row {

                IconButton(
                    modifier = Modifier.scale(1.5f),
                    onClick = {
                        startLauncherRecognizerIntent.launch(recognizerIntent)
                    },
                    content = {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = "Voice Recording"
                        )
                    }
                )
            }




            Spacer(modifier = Modifier)

            recordingUrl?.let {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                ) {
                    ExoplayerCompose(uri  = it )
                }
            }



        }
    }

    BackHandler {
        backStack()
    }


}


@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun PrevSpeechRecognizerCompose(){

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val repository = PlatformObject.getRepository(context)
    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {
        GisMemoTheme {
            Surface(
                modifier = Modifier,
                color = MaterialTheme.colors.onPrimary,
                contentColor = MaterialTheme.colors.primary
            ) {
                Box(
                    modifier = Modifier
                ) {
                    SpeechRecognizerCompose(navController = navController)
                }
            }
        }


    }
}


