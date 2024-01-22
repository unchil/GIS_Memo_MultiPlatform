package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil3.SingletonImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jetbrains.handson.kmm.shared.data.DestinationsLocalDataSource
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.theme.GisMemoTheme
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName
import com.unchil.gismemo_multiplatform.android.common.dpToSize

@SuppressLint("SuspiciousIndentation")
@Composable
fun ImageViewer(
    data:Any,
    size: Size,
    contentScale: ContentScale = ContentScale.FillWidth,
    isZoomable:Boolean = false
){

    val context = LocalContext.current
    var scale = 1f
    var rotationState = 0f

    val boxModifier:Modifier = remember {
        when (isZoomable) {
            true -> {
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, rotation ->
                            scale *= zoom
                            rotationState += rotation
                        }
                    }
            }

            false -> Modifier.fillMaxSize()
        }
    }

    val imageModifier:Modifier = remember {
        when (isZoomable) {
            true -> {
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = maxOf(.5f, minOf(3f, scale)),
                        scaleY = maxOf(.5f, minOf(3f, scale)),
                        rotationZ = rotationState
                    )
            }

            false -> Modifier.fillMaxSize()
        }
    }

    val model =
        ImageRequest.Builder(context).data(data).size(size).crossfade(true).build()


    val transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = {
        when(it){
            is AsyncImagePainter.State.Error -> {
                if(data == ""){
                    AsyncImagePainter.State.Empty
                }else {
                    it
                }
            }
            else -> { it}
        }
    }


    SubcomposeAsyncImage(
        model = model,
        contentDescription = "" ,
        imageLoader = SingletonImageLoader.get(context),
        transform = transform
    ) {

        val painter = this.painter

        when(painter.state){
            is AsyncImagePainter.State.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = boxModifier
                ){
                    CircularProgressIndicator(
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            is AsyncImagePainter.State.Success -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = boxModifier
                ){
                    Image(
                        painter = painter ,
                        contentDescription = "",
                        contentScale = contentScale,
                        modifier = imageModifier
                    )
                }
            }
            AsyncImagePainter.State.Empty -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = boxModifier.padding(10.dp)
                ){

                    Image(
                        painterResource(R.drawable.outline_perm_media_black_48),
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = imageModifier
                    )

                    Text(
                        text = context.getString(R.string.image_load_empty),
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(color = Color.DarkGray.copy(alpha = 0.8f))
                            .padding(10.dp)
                        ,
                        textAlign= TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )

                }

            }
            is AsyncImagePainter.State.Error -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = boxModifier.padding(10.dp)
                    ){
                        Image(
                            painterResource(R.drawable.outline_perm_media_black_48),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = imageModifier
                        )

                        (painter.state as AsyncImagePainter.State.Error).result.throwable.localizedMessage?.let {
                            Text(
                                text =it,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .background(color = Color.DarkGray.copy(alpha = 0.8f))
                                    .padding(10.dp)
                                        ,
                                textAlign= TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                }

        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoPreview(
    modifier: Modifier = Modifier,
    data:Any,
    width: Dp = 100.dp,
    height:Dp = 100.dp,
    onPhotoPreviewTapped: (Any) -> Unit
) {

    Box(
        modifier = Modifier
            .then(modifier)
            .height(height)
            .width(width)
            .border(width = 1.dp, color = Color.Black, shape = ShapeDefaults.Small)
            .clip(shape = ShapeDefaults.Small)
            .combinedClickable { onPhotoPreviewTapped(data) },
        contentAlignment = Alignment.Center

    ) {
        ImageViewer(
            data = data,
            size = LocalContext.current.dpToSize(width = width, height = height) ,
            contentScale = ContentScale.Crop
        )
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun PreviewImageViewer(
    modifier: Modifier = Modifier,
){

    val url = DestinationsLocalDataSource.craneDestinations.find {
        it.city == "MADRID"
    }?.imageUrl ?: ""

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val repository = PlatformObject.getRepository(context)
    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {
        val permissions = listOf(
            Manifest.permission.INTERNET,
        )
        val multiplePermissionsState = rememberMultiplePermissionsState( permissions)
        CheckPermission(multiplePermissionsState = multiplePermissionsState)

        var isGranted by remember { mutableStateOf(true) }
        permissions.forEach { chkPermission ->
            isGranted =   isGranted && multiplePermissionsState.permissions.find {
                it.permission == chkPermission
            }?.status?.isGranted ?: false
        }

        PermissionRequiredCompose(
            isGranted = isGranted,
            multiplePermissions = permissions ,
            viewType = PermissionRequiredComposeFuncName.Weather
        ) {

            val uri = Uri.parse("android.resource://com.unchil.gismemo_multiplatform.android/" + R.drawable.snapshot)
            GisMemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        ImageViewer(data = uri, size = Size.ORIGINAL)
                    //    PhotoPreview(data = url, width = 160.dp, height = 160.dp){   }
                     //  PhotoPreview(data = url, ){   }
                    }


                 }
            }

        }
    }
}