package com.unchil.gismemo_multiplatform.android.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Cabin
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.DownhillSkiing
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.FlightLand
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Kayaking
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalTaxi
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.PublishedWithChanges
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.RollerSkating
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material.icons.outlined.ScubaDiving
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Skateboarding
import androidx.compose.material.icons.outlined.Snowboarding
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.Toll
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jetbrains.handson.kmm.shared.data.WriteMemoDataType
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.navigation.GisMemoDestinations
import io.ktor.http.Url


sealed class MemoData {
    data class Photo(val dataList: MutableList<String>) : MemoData()
    data class SnapShot(val dataList: MutableList<String>) : MemoData()
    data class AudioText(var dataList: MutableList<Pair<String,List<String>>>) : MemoData()
    data class Video(val dataList: MutableList<String>) : MemoData()

}


enum class MemoDataUser {
    DetailMemoView, WriteMemoView
}


fun WriteMemoDataType.getDesc(): Pair<Int, ImageVector>{
    return  when(this){
        WriteMemoDataType.PHOTO -> {
            Pair(R.string.dataContainer_Photo,  Icons.Outlined.Photo)
        }
        WriteMemoDataType.AUDIOTEXT -> {
            Pair(R.string.dataContainer_AudioText,  Icons.Outlined.Mic)
        }
        WriteMemoDataType.VIDEO -> {
            Pair(R.string.dataContainer_Video,  Icons.Outlined.Videocam)
        }
        WriteMemoDataType.SNAPSHOT -> {
            Pair(R.string.dataContainer_Screenshot,  Icons.Outlined.Screenshot)
        }
    }
}

enum class DrawingMenu {
    Draw,Swipe,Eraser
}

val DrawingMenuList = listOf(
    DrawingMenu.Draw,
    DrawingMenu.Swipe,
    DrawingMenu.Eraser
)

fun DrawingMenu.getDesc():Pair<ImageVector, Color> {
    return when(this){
        DrawingMenu.Draw -> {
            Pair( Icons.Outlined.Draw , Color.Red)
        }
        DrawingMenu.Swipe -> {
            Pair( Icons.Outlined.Swipe ,  Color.Red)
        }
        DrawingMenu.Eraser -> {
            Pair( Icons.Outlined.Toll ,  Color.Red)
        }

    }
}

enum class CreateMenu {
    SNAPSHOT,RECORD,CAMERA
}

val CreateMenuList = listOf(
    CreateMenu.SNAPSHOT,
    CreateMenu.RECORD,
    CreateMenu.CAMERA,
)


fun CreateMenu.getDesc():Pair<ImageVector, String?>{
    return  when(this){
        CreateMenu.SNAPSHOT -> {
            Pair(Icons.Outlined.Screenshot,  null)
        }
        CreateMenu.RECORD -> {
            Pair(Icons.Outlined.Mic,  GisMemoDestinations.SpeechToText.route)
        }
        CreateMenu.CAMERA -> {
            Pair(Icons.Outlined.Videocam, GisMemoDestinations.CameraCompose.route)
        }
    }
}

enum class SaveMenu{
    CLEAR,SAVE
}

val SaveMenuList = listOf(
    SaveMenu.CLEAR,
    SaveMenu.SAVE
)

fun SaveMenu.getDesc():Pair<ImageVector, ImageVector?> {
    return when(this){
        SaveMenu.CLEAR -> {
            Pair(Icons.Outlined.Replay,  null)
        }
        SaveMenu.SAVE -> {
            Pair(Icons.Outlined.PublishedWithChanges,  null)
        }
    }
}

enum class SettingMenu{
    SECRET, MARKER,TAG
}

val SettingMenuList = listOf(
    SettingMenu.SECRET,
    SettingMenu.MARKER,
    SettingMenu.TAG
)

fun SettingMenu.getDesc():Pair<ImageVector, ImageVector?> {
    return when(this){
        SettingMenu.SECRET -> {
            Pair(Icons.Outlined.Lock,  Icons.Outlined.LockOpen)
        }
        SettingMenu.MARKER -> {
            Pair(Icons.Outlined.LocationOn,  Icons.Outlined.LocationOff)
        }
        SettingMenu.TAG -> {
            Pair(Icons.Outlined.Class,  null)
        }

    }
}

enum class MapTypeMenu {
    TERRAIN,NORMAL,HYBRID
}

val MapTypeMenuList = listOf(
    MapTypeMenu.TERRAIN,
    MapTypeMenu.NORMAL,
    MapTypeMenu.HYBRID,
)

fun MapTypeMenu.getDesc():Pair<ImageVector, ImageVector?> {
    return when(this){
        MapTypeMenu.NORMAL -> {
            Pair( Icons.Outlined.Map, null)
        }
        MapTypeMenu.TERRAIN -> {
            Pair( Icons.Outlined.Forest, null)
        }
        MapTypeMenu.HYBRID -> {
            Pair( Icons.Outlined.Public, null)
        }
    }
}

enum class SearchOption {
    TITLE, SECRET, MARKER, TAG, DATE
}

fun SearchOption.name():String{
    return when(this){
        SearchOption.TITLE ->  "제목"
        SearchOption.SECRET -> "보안"
        SearchOption.MARKER -> "마커"
        SearchOption.TAG -> "태그"
        SearchOption.DATE -> "날짜"
    }
}

sealed class SearchQueryDataValue {
    data class radioGroupOption(val index:Int) : SearchQueryDataValue()
    data class tagOption(val indexList: ArrayList<Int>): SearchQueryDataValue()
    data class dateOption(val fromToDate:Pair<Long,Long>): SearchQueryDataValue()
    data class titleOption(val title: String): SearchQueryDataValue()
}

typealias QueryData= Pair< SearchOption, SearchQueryDataValue>


