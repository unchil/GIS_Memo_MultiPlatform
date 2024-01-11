package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.FlipCameraIos
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil3.toUri
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory
import com.unchil.gismemo.shared.composables.LocalPermissionsManager
import com.unchil.gismemo.shared.composables.PermissionsManager
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.FileManager
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName
import com.unchil.gismemo_multiplatform.android.navigation.GisMemoDestinations
import com.unchil.gismemo_multiplatform.android.viewModel.CameraViewModel
import com.unchil.gismemo_multiplatform.android.viewModel.SpeechRecognizerViewModel
import io.ktor.http.Url
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Long.fromNanoToSeconds() = (this / (1000 * 1000 * 1000)).toInt()

suspend fun Context.getCameraProvider() : ProcessCameraProvider = suspendCoroutine{ continuation ->
    ProcessCameraProvider.getInstance(this).also { listenableFuture ->
        listenableFuture.addListener(
            {continuation.resume(listenableFuture.get())},
            mainExecutor
        )
    }
}


suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    @ImageCapture.FlashMode  flashMode: Int = ImageCapture.FLASH_MODE_AUTO,
    @TorchState.State  torchState: Int = TorchState.OFF,
): Pair<VideoCapture<Recorder>, ImageCapture> {


    val preview = androidx.camera.core.Preview.Builder().build()

    val qualitySelector = QualitySelector.from( Quality.UHD, FallbackStrategy.lowerQualityOrHigherThan(
        Quality.UHD)
    )

    val recorder = Recorder.Builder().setExecutor(mainExecutor)
        .setQualitySelector(qualitySelector)
        .build()

    val videoCapture = VideoCapture.withOutput(recorder)

    val imageCapture = ImageCapture.Builder()
        .setFlashMode(flashMode)
        .build()


    getCameraProvider().let { cameraProvider ->
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            videoCapture,
            imageCapture
        ).apply {
            cameraControl.enableTorch(torchState == TorchState.ON)

            preview.setSurfaceProvider(previewView.surfaceProvider)

        }

        return Pair(videoCapture, imageCapture)
    }

}


sealed class RecordingStatus {
    object Idle : RecordingStatus()
    object InProgress : RecordingStatus()
    object Paused : RecordingStatus()
}


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("RestrictedApi", "MissingPermission", "UnrememberedMutableState",
    "CoroutineCreationDuringComposition"
)
@Composable
fun CameraCompose( navController: NavController? = null   ) {

    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    val prevViewBottomPaddingValue: Dp
    val prevViewStartPaddingValue: Dp
    val deleteStartPaddingValue: Dp
    var alignment = Alignment.BottomCenter

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {
            alignment =  Alignment.BottomCenter
            prevViewBottomPaddingValue = 80.dp
            prevViewStartPaddingValue = 10.dp
            deleteStartPaddingValue = 110.dp
        }
        else -> {
            alignment =  Alignment.CenterEnd
            prevViewBottomPaddingValue = 10.dp
            prevViewStartPaddingValue = 10.dp
            deleteStartPaddingValue = 110.dp
        }
    }



    val permissions =
        remember { listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO) }
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val repository = LocalRepository.current
    val viewModel = remember { CameraViewModel( repository = repository ) }

    val previewView: PreviewView = remember { PreviewView(context) }
    val videoCapture: MutableState<VideoCapture<Recorder>?> =   mutableStateOf(null)
    val imageCapture: MutableState<ImageCapture?> =  mutableStateOf(null)

    var videoRecording: Recording? = remember { null }

    val recordingStarted: MutableState<Boolean> = remember { mutableStateOf(false) }
    val audiioEnabled: MutableState<Boolean> = remember { mutableStateOf(true) }
    val cameraSelector: MutableState<CameraSelector> =  remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val currentCameraInfo: MutableState<CameraInfo?> = remember { mutableStateOf(null) }
    val torchState: MutableState<Int> = remember { mutableStateOf(TorchState.OFF) }
    val flashMode: MutableState<Int> = remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }
    val recordingStatus: MutableState<RecordingStatus> = remember {  mutableStateOf(RecordingStatus.Idle) }
    var recordingLength: Int by mutableStateOf(0)

    var photoPreviewData: Any by rememberSaveable { mutableStateOf(R.drawable.outline_perm_media_black_48) }

    val isDualCamera: MutableState<Boolean> = remember { mutableStateOf(true) }

    val isVideoRecording = remember{ mutableStateOf(false) }


    lifecycleOwner.lifecycleScope.launch {

        context.createVideoCaptureUseCase(
            lifecycleOwner,
            cameraSelector.value,
            previewView,
            flashMode.value,
            torchState.value
        ).let {
            videoCapture.value = it.first
            imageCapture.value = it.second
        }

        val cameraInfos = context.getCameraProvider().availableCameraInfos
        isDualCamera.value = cameraInfos.size > 1
        currentCameraInfo.value = cameraInfos.find { cameraInfo ->
            cameraInfo.lensFacing == cameraSelector.value.lensFacing
        }

    }

    val currentPhotoList = viewModel._currentPhoto

    val photoList:MutableList<String>
            =  rememberSaveable { mutableListOf() }

    val currentVideoList = viewModel._currentVideo


    val videoList:MutableList<String>
            =  rememberSaveable { mutableListOf()  }

    val findVideoList
            =  rememberSaveable { mutableListOf<Boolean>() }

    val imageCaptureListener =  object : ImageCapture.OnImageSavedCallback {
        override fun onError(error: ImageCaptureException) { }
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            lifecycleOwner.lifecycleScope.launch {
                outputFileResults.savedUri?.let {uri ->

                    photoPreviewData = uri

                    photoList.add( uri.encodedPath ?: "")

                    findVideoList.add(isVideoRecording.value)

                }
            }
        }
    }

    val  takePicture = {
        imageCapture.value?.let {

            FileManager.getFilePath(context = context, outputfile = FileManager.Companion.OUTPUTFILE.IMAGE).let { filePath->
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(filePath)).build()
                it.takePicture( outputFileOptions, context.mainExecutor, imageCaptureListener)
            }
        }
    }

    val videoRecordingListener = Consumer<VideoRecordEvent> { event ->
        when (event) {
            is VideoRecordEvent.Finalize -> {
                if (!event.hasError()) {


                    videoList.add(
                          event.outputResults.outputUri.encodedPath ?: ""
                    )

                    recordingLength = 0
                    videoRecording = null


                    isVideoRecording.value = true
                    takePicture()

                }
            }
            is VideoRecordEvent.Status -> {
                recordingLength = event.recordingStats.recordedDurationNanos.fromNanoToSeconds()
            }
        }
    }


    val takeVideo = {
        if( !recordingStarted.value){
            videoCapture.value?.let { videoCapture ->

                FileManager.getFilePath(context = context, outputfile = FileManager.Companion.OUTPUTFILE.VIDEO).let { filePath ->
                    recordingStatus.value = RecordingStatus.InProgress
                    recordingStarted.value = true
                    val outputFileOptions = FileOutputOptions.Builder(File(filePath)).build()
                    videoRecording = videoCapture.output.prepareRecording(context, outputFileOptions).apply {
                        if (audiioEnabled.value) withAudioEnabled()
                    } .start(context.mainExecutor, videoRecordingListener)
                }

            }
        }
    }

    val backStack = {

        findVideoList.forEachIndexed { index, isVideo ->
            if(!isVideo){
                currentPhotoList.add(photoList[index] )
            }
        }

        videoList.forEach {
            currentVideoList.add(it)
        }

        viewModel.onEvent(CameraViewModel.Event.SetPhotoVideo(currentPhotoList, currentVideoList))
        navController?.popBackStack()
    }

    var isGranted by mutableStateOf(true)
    permissions.forEach { chkPermission ->
        isGranted =  isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission }?.status?.isGranted
                ?: false
    }


    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions,
        viewType = PermissionRequiredComposeFuncName.Camera,
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize(),
                update = {

                }
            )

            if (videoRecording == null) {

                IconButton(
                    modifier = Modifier.align(Alignment.TopCenter),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = Color.Gray.copy(alpha = 0.7f) ,
                        contentColor = Color.Black
                    ),
                    onClick = {
                     //   hapticProcessing()
                        torchState.value = when (torchState.value) {
                            TorchState.OFF -> TorchState.ON
                            else -> TorchState.OFF
                        }
                        flashMode.value = when (flashMode.value) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    content = {

                        val imageVector = when (torchState.value) {
                            TorchState.ON -> {
                                Icons.Outlined.FlashOn
                            }
                            else -> {
                                Icons.Outlined.FlashOff
                            }
                        }
                        Icon(
                            imageVector = imageVector,
                            contentDescription = "flash_mode"
                        )
                    }
                )


            } else {
                if (recordingStarted.value) {

                    if (recordingLength > 0) {

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(vertical = 30.dp)
                                .clip(ShapeDefaults.Small)
                        ) {
                            Text(
                                text = DateUtils.formatElapsedTime(recordingLength.toLong()),
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                modifier = Modifier
                                    .background(color = Color.Red.copy(alpha = 0.3f))
                                    .padding(horizontal = 6.dp)
                            )
                        }

                    }


                }
            }


            when(photoPreviewData){
                is Int -> { }
                else -> {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(
                                start = prevViewStartPaddingValue,
                                bottom = prevViewBottomPaddingValue
                            )
                    ) {
                        AnimatedVisibility(findVideoList.size > 0) {

                            PhotoPreview(
                                modifier = Modifier.align(Alignment.BottomStart),
                                data = photoPreviewData,
                                onPhotoPreviewTapped = { it:Any ->

                              //      hapticProcessing()

                                    when (it) {
                                        is Int -> {}
                                        else -> {

                                            if(findVideoList.last()) {
                                                videoList.last().let {videoUri ->
                                                    navController?.navigate(
                                                        GisMemoDestinations.ExoPlayerView.createRoute(
                                                            videoUri
                                                        )
                                                    )
                                                }
                                            } else {
                                                navController?.navigate(
                                                    GisMemoDestinations.PhotoPreview.createRoute(
                                                        it
                                                    )
                                                )
                                            }

                                        }
                                    }
                                }
                            )


                            IconButton(
                                modifier = Modifier
                                    .scale(1f)
                                    .align(Alignment.BottomEnd)
                                    .padding(
                                        start = deleteStartPaddingValue,
                                    ),
                                colors = IconButtonDefaults.outlinedIconButtonColors(
                                    containerColor = Color.Gray.copy(alpha = 0.7f) ,
                                    contentColor = Color.Black
                                ),
                                onClick = {

                                    if (findVideoList.last()) {
                                        videoList.removeAt(videoList.lastIndex)
                                        photoList.removeAt(photoList.lastIndex)
                                    } else {
                                        photoList.removeAt(photoList.lastIndex)
                                    }
                                    findVideoList.removeAt(findVideoList.lastIndex)


                                    photoPreviewData = if (findVideoList.isEmpty()) {
                                     //   mutableIntStateOf(R.drawable.outline_perm_media_black_48)
                                       R.drawable.outline_perm_media_black_48
                                    } else {
                                        photoList.last().toUri()
                                    }


                                },
                                content = {

                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete"
                                    )

                                }
                            )



                        }
                    }





                }
            }






            VideoCameraFooter(
                modifier = Modifier.align(alignment),
                recordingStatus = recordingStatus.value,
                showFlipIcon = isDualCamera.value,
                onRecordTapped = {
              //      hapticProcessing()
                    takeVideo()
                },
                onPauseTapped = {
             //       hapticProcessing()
                    videoRecording?.pause()
                    recordingStatus.value = RecordingStatus.Paused
                },
                onResumeTapped = {
              //      hapticProcessing()
                    videoRecording?.resume()
                    recordingStatus.value = RecordingStatus.InProgress
                },
                onStopTapped = {
            //        hapticProcessing()
                    videoRecording?.stop()
                    recordingStarted.value = false
                    recordingStatus.value = RecordingStatus.Idle
                },
                onFlipTapped = {
             //       hapticProcessing()
                    if(videoRecording == null ) {
                        cameraSelector.value = when (cameraSelector.value) {
                            CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
                            else -> CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    }
                },
                onCaptureTapped = {
                 //   hapticProcessing()

                    isVideoRecording.value = false
                    takePicture()
                }
            )


        }



    }

    BackHandler {
        backStack()
    }



}


@Composable
fun VideoCameraFooter(
    modifier: Modifier = Modifier,
    recordingStatus: RecordingStatus,
    showFlipIcon: Boolean,
    onRecordTapped: () -> Unit,
    onStopTapped: () -> Unit,
    onPauseTapped: () -> Unit,
    onResumeTapped: () -> Unit,
    onFlipTapped: () -> Unit,
    onCaptureTapped: () -> Unit,
) {


    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> {

            Row(
                modifier = Modifier
                    .background(color = Color.White.copy(alpha = 0.2f))
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 10.dp)
                    .then(modifier),
                horizontalArrangement =  Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically

            ) {


                when (recordingStatus) {

                    RecordingStatus.Idle -> {
                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = {onRecordTapped()} ,
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.Videocam,
                                    contentDescription = "videocam"
                                )
                            })
                    }

                    RecordingStatus.InProgress -> {

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onPauseTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.PauseCircle,
                                    contentDescription = "PauseCircle"
                                )
                            }
                        )

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onStopTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.StopCircle,
                                    contentDescription = "StopCircle"
                                )
                            }
                        )
                    }


                    RecordingStatus.Paused -> {

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onResumeTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.PlayCircle,
                                    contentDescription = "PlayCircle"
                                )
                            }
                        )

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onStopTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.StopCircle,
                                    contentDescription = "StopCircle"
                                )
                            }
                        )

                    }

                    else -> {}
                }

                androidx.compose.material.IconButton(
                    modifier = Modifier.scale(1.5f),
                    onClick = { onCaptureTapped() },
                    content = {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.Camera,
                            contentDescription = "capture"
                        )
                    }
                )

                if (showFlipIcon && recordingStatus == RecordingStatus.Idle) {

                    androidx.compose.material.IconButton(
                        modifier = Modifier.scale(1.5f),
                        onClick = { onFlipTapped() },
                        content = {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Outlined.FlipCameraIos,
                                contentDescription = "camera_flip"
                            )
                        }
                    )
                }

            }

        }
        else -> {
            Column(
                modifier = Modifier
                    .background(color = Color.White.copy(alpha = 0.2f))
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .padding(horizontal = 10.dp)
                    .then(modifier),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {


                when (recordingStatus) {

                    RecordingStatus.Idle -> {

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = {onRecordTapped()} ,
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.Videocam,
                                    contentDescription = "videocam"
                                )
                            })
                    }

                    RecordingStatus.InProgress -> {

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onPauseTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.PauseCircle,
                                    contentDescription = "PauseCircle"
                                )
                            }
                        )

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onStopTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.StopCircle,
                                    contentDescription = "StopCircle"
                                )
                            }
                        )
                    }

                    RecordingStatus.Paused -> {

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onResumeTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.PlayCircle,
                                    contentDescription = "PlayCircle"
                                )
                            }
                        )

                        androidx.compose.material.IconButton(
                            modifier = Modifier.scale(1.5f),
                            onClick = { onStopTapped() },
                            content = {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Outlined.StopCircle,
                                    contentDescription = "StopCircle"
                                )
                            }
                        )

                    }

                    else -> {}
                }

                androidx.compose.material.IconButton(
                    modifier = Modifier.scale(1.5f),
                    onClick = { onCaptureTapped() },
                    content = {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Outlined.Camera,
                            contentDescription = "capture"
                        )
                    }
                )

                if (showFlipIcon && recordingStatus == RecordingStatus.Idle) {

                    androidx.compose.material.IconButton(
                        modifier = Modifier.scale(1.5f),
                        onClick = { onFlipTapped() },
                        content = {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Outlined.FlipCameraIos,
                                contentDescription = "camera_flip"
                            )
                        }
                    )

                }

            }

        }
    }






}


@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun CameraComposePreview(){
    val permissionsManager = PermissionsManager()
    val navController = rememberAnimatedNavController()

    CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {
        MyApplicationTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                CameraCompose(
                    navController = navController
                )

            }
        }
    }
}



