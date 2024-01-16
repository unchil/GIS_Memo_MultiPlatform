package com.unchil.gismemo_multiplatform.android.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.PublishedWithChanges
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material.icons.outlined.Toll
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.jetbrains.handson.kmm.shared.data.WriteMemoData
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.navigation.GisMemoDestinations


sealed class MemoData {
    data class Photo(val dataList: MutableList<String>) : MemoData()
    data class SnapShot(val dataList: MutableList<String>) : MemoData()
    data class AudioText(var dataList: MutableList<Pair<String,List<String>>>) : MemoData()
    data class Video(val dataList: MutableList<String>) : MemoData()

}


enum class MemoDataUser {
    DetailMemoView, WriteMemoView
}



fun WriteMemoDataDesc(type: WriteMemoData.Type): Pair<Int, ImageVector>{
    return when(type){
        WriteMemoData.Type.PHOTO -> {
            Pair( R.string.dataContainer_Photo,  Icons.Outlined.Photo)
        }
        WriteMemoData.Type.AUDIOTEXT -> {
            Pair(R.string.dataContainer_AudioText,  Icons.Outlined.Mic)
        }
        WriteMemoData.Type.VIDEO -> {
            Pair(R.string.dataContainer_Video,  Icons.Outlined.Videocam)
        }
        WriteMemoData.Type.SNAPSHOT -> {
            Pair(R.string.dataContainer_Screenshot,  Icons.Outlined.Screenshot)
        }
    }
}

object DrawingMenuData {
    enum class Type {
        Draw,Swipe,Eraser
    }
    val Types = listOf(
        Type.Draw,
        Type.Swipe,
        Type.Eraser
    )
    fun desc(type:Type):Pair<ImageVector, Color> {
        return when(type){
            Type.Draw -> {
                Pair( Icons.Outlined.Draw , Color.Red)
            }
            Type.Swipe -> {
                Pair( Icons.Outlined.Swipe ,  Color.Red)
            }
            Type.Eraser -> {
                Pair( Icons.Outlined.Toll ,  Color.Red)
            }
        }
    }
}

object CreateMenuData {
    enum class Type {
        SNAPSHOT,RECORD,CAMERA
    }

    val Types = listOf(
        Type.SNAPSHOT,
        Type.RECORD,
        Type.CAMERA,
    )

    fun desc(type: Type):Pair<ImageVector, String?>{
         return  when(type){
              Type.SNAPSHOT -> {
                  Pair(Icons.Outlined.Screenshot,  null)
              }
              Type.RECORD -> {
                  Pair(Icons.Outlined.Mic,  GisMemoDestinations.SpeechToText.route)
              }
              Type.CAMERA -> {
                  Pair(Icons.Outlined.Videocam, GisMemoDestinations.CameraCompose.route)
              }
          }
    }
}


object SaveMenuData {
    enum class Type{
        CLEAR,SAVE
    }
    val Types = listOf(
        Type.CLEAR,
        Type.SAVE
    )
    fun desc(type:Type):Pair<ImageVector, ImageVector?> {
        return when(type){
            Type.CLEAR -> {
                Pair(Icons.Outlined.Replay,  null)
            }
            Type.SAVE -> {
                Pair(Icons.Outlined.PublishedWithChanges,  null)
            }
        }
    }
}


object SettingMenuData{
    enum class Type {
        SECRET, MARKER,TAG
    }
    val Types = listOf(
        Type.SECRET,
        Type.MARKER,
        Type.TAG
    )
    fun desc(type:Type):Pair<ImageVector, ImageVector?> {
        return when(type){
            Type.SECRET -> {
                Pair(Icons.Outlined.Lock,  Icons.Outlined.LockOpen)
            }
            Type.MARKER -> {
                Pair(Icons.Outlined.LocationOn,  Icons.Outlined.LocationOff)
            }
            Type.TAG -> {
                Pair(Icons.Outlined.Class,  null)
            }
        }
    }
}

object MapTypeMenuData {
    enum class Type {
        TERRAIN,NORMAL,HYBRID
    }
    val Types = listOf(
        Type.TERRAIN,
        Type.NORMAL,
        Type.HYBRID,
    )
    fun desc(type:Type):Pair<ImageVector, ImageVector?> {
        return when(type){
            Type.TERRAIN -> {
                Pair( Icons.Outlined.Forest, null)
            }
            Type.NORMAL -> {
                Pair( Icons.Outlined.Map, null)
            }
            Type.HYBRID -> {
                Pair( Icons.Outlined.Public, null)
            }
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

data class RadioGroupOption (
    val title:String,
    val options:List<String>,
    val icon:ImageVector? = null,
    val iconDesc:String? = null ,
    val contents:@Composable() (() -> Unit)? = null
)



