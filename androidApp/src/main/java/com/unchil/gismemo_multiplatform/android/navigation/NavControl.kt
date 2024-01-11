package com.unchil.gismemo_multiplatform.android.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

fun NavHostController.navigateTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }


sealed class GisMemoDestinations(
    val route:String,
    val name:Int = 0,
    val icon: ImageVector? = null,

    ){

    object SpeechToText : GisMemoDestinations ( route = "voicerecording")

    object CameraCompose : GisMemoDestinations ( route = "camerapreview")

    object PhotoPreview : GisMemoDestinations( route = "photopreview?${ARG_NAME_FILE_PATH}={$ARG_NAME_FILE_PATH}") {

        fun createRoute(filePath: Any): String {
            val path = when(filePath){
                is Int -> {
                    Uri.parse("android.resource://com.unchil.gismemo_multiplatform.android/" + filePath.toString()).toString()
                }
                else -> {
                    ( filePath as Uri).encodedPath
                }
            }
            return "photopreview?${ARG_NAME_FILE_PATH}=${path}"
        }

        val createRouteNew: (filePath: Any) ->  String  = { filePath ->
            val path = when(filePath){
                is Int -> {
                    Uri.parse("android.resource://com.unchil.gismemo_multiplatform.android/" + filePath.toString()).toString()
                }
                else -> {
                    ( filePath as Uri).encodedPath
                }
            }
            "photopreview?${ARG_NAME_FILE_PATH}=${path}"
        }


        fun getUriFromArgs(bundle: Bundle?): String {
            return bundle?.getString(ARG_NAME_FILE_PATH) ?: ""
        }
    }


    object ExoPlayerView : GisMemoDestinations( route = "exoplayerview?${ARG_NAME_FILE_PATH}={$ARG_NAME_FILE_PATH}&${ARG_NAME_ISVISIBLE_AMPLITUDES}={$ARG_NAME_ISVISIBLE_AMPLITUDES}"  ) {

        fun createRoute(filePath: String, isVisibleAmplitudes:Boolean = false): String {
            return "exoplayerview?${ARG_NAME_FILE_PATH}=${filePath}&${ARG_NAME_ISVISIBLE_AMPLITUDES}=${isVisibleAmplitudes}"
        }

        fun getUriFromArgs(bundle: Bundle?): String {
            return  bundle?.getString(ARG_NAME_FILE_PATH) ?: ""
        }

        fun getIsVisibleAmplitudesFromArgs(bundle: Bundle?): Boolean {
            return  bundle?.getBoolean(ARG_NAME_ISVISIBLE_AMPLITUDES) ?: false
        }
    }

    companion object {
        const val ARG_NAME_ID: String = "id"
        const val ARG_NAME_FILE_PATH: String = "url"
        const val ARG_NAME_ISVISIBLE_AMPLITUDES:String = "isvisibleamplitudes"
    }



}