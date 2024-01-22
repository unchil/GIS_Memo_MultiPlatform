package com.unchil.gismemo_multiplatform.android.view


import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.unchil.gismemo_multiplatform.android.theme.GisMemoTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun Context.getExoPlayer(exoPlayerListener: Player.Listener): ExoPlayer {
    return ExoPlayer.Builder(this).build().apply {
        addListener(exoPlayerListener)
        prepare()
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun  ExoplayerCompose(
    uri: String? = null,
    uriList: List<String> = emptyList(),
    isVisibleAmplitudes:Boolean = false,
    setTrackIndex:((ExoPlayer)->Unit)? = null,
    trackInfo:((Int)->Unit)? = null ){

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var waveformProgress by remember { mutableStateOf(0F) }

    var mediaItemDuration by remember { mutableStateOf(0L) }

    var mediaItemTitle  by  remember { mutableStateOf( "" ) }

    val mediaItems :MutableList<MediaItem> = mutableListOf()



    lateinit var coroutineJob: Job


    val exoPlayerListener = object : Player.Listener {

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            if(isVisibleAmplitudes) {
                waveformProgress = newPosition.positionMs.toFloat() / mediaItemDuration
            }
        }


        override fun onEvents(player: Player, events: Player.Events) {

            if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)){
                if(isVisibleAmplitudes) {
                    if (player.isPlaying) {
                        coroutineJob = lifecycleOwner.lifecycleScope.launch {
                            while (true) {
                                waveformProgress =
                                    player.currentPosition.toFloat() / player.duration
                                delay(100L)
                            }
                        }
                    } else {
                        if (coroutineJob.isActive) {
                            coroutineJob.cancel()
                        }
                    }
                }
            }


            if ( events.contains(Player.EVENT_TRACKS_CHANGED) ) {
                super.onEvents(player, events)

                trackInfo?.let {
                    it(player.currentMediaItemIndex)
                }

                player.playWhenReady = false
                player.seekTo(0L)
                mediaItemDuration = player.duration
                player.currentMediaItem?.localConfiguration?.uri?.let { uri ->
                    mediaItemTitle =
                        "No.${player.currentMediaItemIndex + 1} Track ${uri.lastPathSegment.toString()}"
                }

            }

        }

    }


    val exoPlayer =   remember { context.getExoPlayer(exoPlayerListener) }

    setTrackIndex?.invoke(exoPlayer)


    LaunchedEffect(key1 = uri, key2 = uriList){

        uri?.let {
            if (exoPlayer.mediaItemCount > 0) {
                exoPlayer.addMediaItem(exoPlayer.mediaItemCount , MediaItem.fromUri(it))
                exoPlayer.seekTo(exoPlayer.mediaItemCount - 1, 0)
            } else {
                exoPlayer.setMediaItem(MediaItem.fromUri(it))
            }
        }

        if(uriList.isNotEmpty()){
            uriList.forEach {
                mediaItems.add(MediaItem.fromUri(it))
            }
            exoPlayer.setMediaItems(mediaItems)
        }
    }


    Column {

        if(mediaItemTitle.isNotEmpty()){
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = mediaItemTitle,
                textAlign = TextAlign.Center
            )
        }


        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { context ->

                    PlayerView(context).apply {
                        player = exoPlayer
                        this.controllerShowTimeoutMs = 0

                        val params = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        layoutParams = params
                    }

                },
                modifier = Modifier.fillMaxSize()
            )
        }

        DisposableEffect(key1 = exoPlayer){
            onDispose {
                exoPlayer.release()
            }
        }


    }

}



@Preview
@Composable
private fun PrevExoplayerCompose(){

    val uriList = listOf<String>(
        "/data/data/com.unchil.gismemo_multiplatform.android/files/videos/test.mp4"
    )




    GisMemoTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ExoplayerCompose( uriList = uriList)
        }

    }

}

