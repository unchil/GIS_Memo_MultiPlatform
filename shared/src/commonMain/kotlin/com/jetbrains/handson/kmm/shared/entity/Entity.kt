package com.jetbrains.handson.kmm.shared.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CURRENTLOCATION_TBL(
    @SerialName("dt")
    var dt: Long,
    @SerialName("latitude")
    var latitude: Float,
    @SerialName("longitude")
    var longitude: Float,
    @SerialName("altitude")
    var altitude: Float
)

@Serializable
data class CURRENTWEATHER_TBL(
//        var writeTime: Long,
    @SerialName("dt")
    var dt: Long,
    @SerialName("base")
    var base: String ,
    @SerialName("visibility")
    var visibility: Int,
    @SerialName("timezone")
    var timezone: Long,
    @SerialName("name")
    var name: String,
    @SerialName("latitude")
    var latitude: Float,
    @SerialName("longitude")
    var longitude: Float,
    //       var altitude: Float,
    @SerialName("main")
    var main: String,
    @SerialName("description")
    var description: String,
    @SerialName("icon")
    var icon : String,
    @SerialName("temp")
    var temp: Float,
    @SerialName("feels_like")
    var feels_like: Float,
    @SerialName("pressure")
    var pressure: Float,
    @SerialName("humidity")
    var humidity: Float,
    @SerialName("temp_min")
    var temp_min: Float,
    @SerialName("temp_max")
    var temp_max: Float,
    @SerialName("speed")
    var speed : Float,
    @SerialName("deg")
    var deg : Float,
    @SerialName("all")
    var all  : Int,
    @SerialName("type")
    var type : Int,
    @SerialName("country")
    var country : String,
    @SerialName("sunrise")
    var sunrise : Long,
    @SerialName("sunset")
    var sunset : Long

)




@Serializable
data class MEMO_TBL(
    @SerialName("id")
    var id: Long,
    @SerialName("latitude")
    var latitude: Float,
    @SerialName("longitude")
    var longitude: Float,
    @SerialName("altitude")
    var altitude: Float,
    @SerialName("isSecret")
    var isSecret: Boolean,
    @SerialName("isPin")
    var isPin: Boolean,
    @SerialName("title")
    var title: String,
    @SerialName("snippets")
    var snippets: String,
    @SerialName("desc")
    var desc: String,
    @SerialName("snapshot")
    var snapshot: String,
    @SerialName("snapshotCnt")
    var snapshotCnt: Int,
    @SerialName("textCnt")
    var textCnt: Int,
    @SerialName("photoCnt")
    var photoCnt: Int,
    @SerialName("videoCnt")
    var videoCnt: Int

)

@Serializable
data class MEMO_FILE_TBL(
    @SerialName("id")
    var id: Long,
    @SerialName("type")
    var type: String,
    @SerialName("index")
    var index:Int,
    @SerialName("subIndex")
    var subIndex:Int,
    @SerialName("filePath")
    var filePath: String
)

@Serializable
data class MEMO_TEXT_TBL(
    @SerialName("id")
    var id: Long,
    @SerialName("index")
    var index:Int,
    @SerialName("comment")
    var comment:String
)

@Serializable
data class MEMO_TAG_TBL(
    @SerialName("id")
    var id: Long,
    @SerialName("index")
    var index:Int,
)


@Serializable
data class MEMO_WEATHER_TBL(
    @SerialName("id")
    var id: Long,
    @SerialName("base")
    var base: String ,
    @SerialName("visibility")
    var visibility: Int,
    @SerialName("timezone")
    var timezone: Long,
    @SerialName("name")
    var name: String,
    @SerialName("latitude")
    var latitude: Float,
    @SerialName("longitude")
    var longitude: Float,
    //       var altitude: Float,
    @SerialName("main")
    var main: String,
    @SerialName("description")
    var description: String,
    @SerialName("icon")
    var icon : String,
    @SerialName("temp")
    var temp: Float,
    @SerialName("feels_like")
    var feels_like: Float,
    @SerialName("pressure")
    var pressure: Float,
    @SerialName("humidity")
    var humidity: Float,
    @SerialName("temp_min")
    var temp_min: Float,
    @SerialName("temp_max")
    var temp_max: Float,
    @SerialName("speed")
    var speed : Float,
    @SerialName("deg")
    var deg : Float,
    @SerialName("all")
    var all  : Int,
    @SerialName("type")
    var type : Int,
    @SerialName("country")
    var country : String,
    @SerialName("sunrise")
    var sunrise : Long,
    @SerialName("sunset")
    var sunset : Long

)


@Serializable
data class TAG_CODE_TBL(
    @SerialName("seq")
    var seq: Long?,
    @SerialName("iconName")
    var iconName:String
)

data class LatLngAlt (
    var collectTime:Long,
    var latitude:Float,
    var longitude: Float,
    var altitude: Float = 0f
)

data class DrawingPolyline (
    var polylinelinks: MutableList<LatLngAlt>
)


enum class WriteMemoDataType {
    PHOTO,AUDIOTEXT,VIDEO,SNAPSHOT
}

val WriteMemoDataTypeList = listOf(
    WriteMemoDataType.SNAPSHOT,
    WriteMemoDataType.AUDIOTEXT,
    WriteMemoDataType.PHOTO,
    WriteMemoDataType.VIDEO
)

