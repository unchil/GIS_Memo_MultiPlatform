package com.unchil.gismemo_multiplatform.android.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewStream
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.unchil.gismemo_multiplatform.android.R

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

    object MemoListScreen : GisMemoDestinations(
        route = "list",
        name = R.string.mainmenu_list,
        icon = Icons.Outlined.ViewStream
    )

    object WriteMemoScreen : GisMemoDestinations(
        route = "write",
        name = R.string.mainmenu_write,
        icon = Icons.Outlined.EditNote
    )

    object MapScreen : GisMemoDestinations(
        route = "map",
        name = R.string.mainmenu_map,
        icon = Icons.Outlined.Map
    )

    object SettingScreen : GisMemoDestinations(
        route = "setting",
        name = R.string.mainmenu_setting,
        icon = Icons.Outlined.Settings
    )

    object SpeechRecognizer : GisMemoDestinations (
        route = "speechrecognizer"
    )

    object Camera : GisMemoDestinations (
        route = "camera"
    )

    object ImageViewer : GisMemoDestinations(
        route = "imageviewer?$ARG_NAME_FILE_PATH=$ARG_NAME_FILE_PATH"
    ) {
        fun createRoute(filePath: Any): String {
            val path = when(filePath){
                is Int -> {
                    Uri.parse(
                        "android.resource://com.unchil.gismemo_multiplatform.android/"
                                + filePath.toString()
                    ).toString()
                }
                else -> {
                    ( filePath as Uri).encodedPath
                }
            }
            return "imageviewer?$ARG_NAME_FILE_PATH=$path"
        }
        fun getUriFromArgs(bundle: Bundle?): String {
            return bundle?.getString(ARG_NAME_FILE_PATH) ?: ""
        }

    }

    object ExoPlayer : GisMemoDestinations(
        route = "exoplayer?$ARG_NAME_FILE_PATH=$ARG_NAME_FILE_PATH"
    ) {
        fun createRoute(filePath: String): String {
            return "exoplayer?$ARG_NAME_FILE_PATH=$filePath"
        }
        fun getUriFromArgs(bundle: Bundle?): String {
            return  bundle?.getString(ARG_NAME_FILE_PATH) ?: ""
        }
    }

    object DetailMemo : GisMemoDestinations(
        route = "detailmemo?$ARG_NAME_ID=$ARG_NAME_ID"
    ){
        fun createRoute(id: String) :String {
            return "detailmemo?$ARG_NAME_ID=$id"
        }
        fun getIDFromArgs(bundle: Bundle?): String {
            return bundle?.getString(ARG_NAME_ID) ?: ""
        }
    }

    companion object {
        const val ARG_NAME_ID: String = "id"
        const val ARG_NAME_FILE_PATH: String = "url"
    }

}