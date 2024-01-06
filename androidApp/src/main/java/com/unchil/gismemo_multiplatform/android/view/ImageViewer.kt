package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.gismemo.shared.composables.LocalPermissionsManager
import com.unchil.gismemo.shared.composables.PermissionsManager
import com.unchil.gismemo_multiplatform.android.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName

@SuppressLint("SuspiciousIndentation")
@Composable
fun ImageViewer(data:Any, size: Size, isZoomable:Boolean = false){

    val scale = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(0f) }

    val boxModifier: Modifier = when(isZoomable) {
        true -> {
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale.value *= zoom
                        rotationState.value += rotation
                    }
                }
        }
        false -> Modifier.fillMaxSize()
    }

    val imageModifier: Modifier = when(isZoomable) {
        true -> {
            Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = maxOf(.5f, minOf(3f, scale.value)),
                    scaleY = maxOf(.5f, minOf(3f, scale.value)),
                    rotationZ = rotationState.value
                )
        }
        false -> Modifier.fillMaxSize()
    }

    val context = LocalContext.current

    val model =  ImageRequest.Builder(context)
        .also {
            it.data(data)
            it.size(size)
            it.crossfade(true)
        }.build()


/*
    val imageLoader = ImageLoader.Builder(context)
            .components{
                add(NetworkFetcher.Factory())
            }
            .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.25)
                        .build()

            }.apply {
                logger(DebugLogger())
            }.build()
*/


        SubcomposeAsyncImage(
            model = model,
            contentDescription = "" ,
            imageLoader = SingletonImageLoader.get(context),
        ) {

            val painter = this.painter

            when(painter.state){

                is AsyncImagePainter.State.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = boxModifier
                    ){
                        androidx.compose.material.CircularProgressIndicator(
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
                            contentScale = ContentScale.FillWidth,
                            modifier = imageModifier
                        )
                    }
                }
                else -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = boxModifier
                    ){
                        Image(
                             painterResource(R.drawable.outline_perm_media_black_48),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = imageModifier
                        )
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
    onPhotoPreviewTapped: (Any) -> Unit
) {

    Box(
        modifier = Modifier
            .then(modifier)
            .height(100.dp)
            .width(100.dp)
            .border(width = 1.dp, color = Color.Black, shape = ShapeDefaults.Small)
            .clip(shape = ShapeDefaults.Small)
            .combinedClickable { onPhotoPreviewTapped(data) }
        ,
        contentAlignment = Alignment.Center

    ) {
        ImageViewer(data = data, size = Size.ORIGINAL, isZoomable = false)
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun PreviewImageViewer(
    modifier: Modifier = Modifier,
){

    val  url1 = Uri.parse("android.resource://com.unchil.gismemo_multiplatform.android/" + R.drawable.outline_perm_media_black_48).toString().toUri()
    val url2 = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format"

    val permissionsManager = PermissionsManager()

    CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {

        val permissions = listOf(
            Manifest.permission.INTERNET,
        )
        val multiplePermissionsState = rememberMultiplePermissionsState( permissions)
        CheckPermission(multiplePermissionsState = multiplePermissionsState)

        var isGranted by mutableStateOf(true)
        permissions.forEach { chkPermission ->
            isGranted =   isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission  }?.status?.isGranted ?: false
        }


        PermissionRequiredCompose(
            isGranted = isGranted,
            multiplePermissions = permissions ,
            viewType = PermissionRequiredComposeFuncName.Weather
        ) {

MyApplicationTheme {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
                ImageViewer(data = url2, size = Size.ORIGINAL, true)
}}
        }

    }
}