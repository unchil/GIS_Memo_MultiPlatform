package com.jetbrains.handson.kmm.shared.entity

import com.jetbrains.handson.kmm.shared.cache.CURRENTWEATHER_TABLE
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
data class CurrentWeather(
    @SerialName("coord") var coord: Coord,
    @SerialName("weather") var weather: List<Weather>,
    @SerialName("base") var base: String, //Internal parameter
    @SerialName("main") var main: Main,
    @SerialName("visibility") var visibility: Int, // visibility
    @SerialName("wind") var wind: Wind,
    @SerialName("clouds") var clouds: Clouds,
    @SerialName("dt") var dt: Long, //Time of data calculation, unix, UTC
    @SerialName("sys") var sys: Sys,
    @SerialName("timezone") var timezone: Long, //Shift in seconds from UTC
    @SerialName("id") var id: Long, //City ID
    @SerialName("name") var name: String, //City name
    @SerialName("cod") var cod: Int // Return Result Code
//   var rain: Rain,
//   var snow: Snow,

){
    fun toCURRENTWEATHER_TABLE(): CURRENTWEATHER_TABLE {

        val currentWeatherTbl = CURRENTWEATHER_TABLE(
            dt = this.dt,
            base = this.base,
            visibility = this.visibility.toString(),
            timezone = this.timezone.toString(),
            name = this.name,
            latitude = this.coord.lat.toString(),
            longitude = this.coord.lon.toString(),
            main = this.weather[0].main,
            description = this.weather[0].description,
            icon = this.weather[0].icon,
            temp = this.main.temp.toString(),
            feels_like = this.main.feels_like.toString(),
            pressure = this.main.pressure.toString(),
            humidity = this.main.humidity.toString(),
            temp_min = this.main.temp_min.toString(),
            temp_max = this.main.temp_max.toString(),
            speed = this.wind.speed.toString(),
            deg = this.wind.deg.toString(),
            aa = this.clouds.all.toString(),
            //  type = this.sys.type,
            type = "0",
            country = this.sys.country.toString(),
            sunrise = this.sys.sunrise.toString(),
            sunset = this.sys.sunset.toString()
        )

        return currentWeatherTbl
    }

    fun toCURRENTWEATHER_TBL(): CURRENTWEATHER_TBL {

        val currentWeatherTbl = CURRENTWEATHER_TBL(
            dt = this.dt,
            base = this.base,
            visibility = this.visibility,
            timezone = this.timezone,
            name = this.name,
            latitude = this.coord.lat,
            longitude = this.coord.lon,
            main = this.weather[0].main,
            description = this.weather[0].description,
            icon = this.weather[0].icon,
            temp = this.main.temp,
            feels_like = this.main.feels_like,
            pressure = this.main.pressure,
            humidity = this.main.humidity,
            temp_min = this.main.temp_min,
            temp_max = this.main.temp_max,
            speed = this.wind.speed,
            deg = this.wind.deg,
            all = this.clouds.all,
            //  type = this.sys.type,
            type = 0,
            country = this.sys.country,
            sunrise = this.sys.sunrise,
            sunset = this.sys.sunset
        )

        return currentWeatherTbl
    }


}


@Serializable
data class Coord (
    @SerialName("lon") var lon: Float, //City geo location, longitude
    @SerialName("lat") var lat: Float //City geo location, latitude
)

@Serializable
data class Weather ( //more info Weather condition codes

    @SerialName("id") var id: Int, //Weather condition id
    @SerialName("main") var main: String, //Group of weather parameters (Rain, Snow, Extreme etc.)
    @SerialName("description") var description: String, //Weather condition within the group. You can get the
    @SerialName("icon") var icon : String //Weather icon id
)
@Serializable
data class Main (
    @SerialName("temp") var temp: Float, //Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    @SerialName("feels_like") var feels_like: Float, //Temperature. This temperature parameter accounts for the human perception of weather. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    @SerialName("pressure") var pressure: Float, //Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
    @SerialName("humidity") var humidity: Float, //Humidity, %
    @SerialName("temp_min") var temp_min: Float, //Minimum temperature at the moment. This is minimal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    @SerialName("temp_max") var temp_max: Float //Maximum temperature at the moment. This is maximal currently observed temperature (within large megalopolises and urban areas). Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
    //  var sea_level: String, //Atmospheric pressure on the sea level, hPa
    //  var grnd_level: String //Atmospheric pressure on the ground level, hPa
)
@Serializable
data class Wind (
    @SerialName("speed") var speed : Float, //Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
    @SerialName("deg") var deg : Float //Wind direction, degrees (meteorological)
    //  var gust : String //Wind gust. Unit Default: meter/sec, Metric: meter/sec, Imperial:miles/hour
)
@Serializable
data class Clouds (
    @SerialName("all") var all  : Int //Cloudiness, %
)
@Serializable
data class Sys (
    //   var type : Int, //Internal parameter
//    var id : Int, //Internal parameter
    @SerialName("country") var country : String, //Country code (GB, JP etc.)
    @SerialName("sunrise") var sunrise : Long, //Sunrise time, unix, UTC
    @SerialName("sunset") var sunset : Long //Sunset time, unix, UTC
    //    var message : Float, //Internal parameter
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

) {
    fun toCURRENTWEATHER_TBL(): CURRENTWEATHER_TBL {
        return CURRENTWEATHER_TBL(
            dt = this.id,
            base = this.base,
            visibility = this.visibility,
            timezone = this.timezone,
            name = this.name,
            latitude = this.latitude,
            longitude = this.longitude,
            main = this.main,
            description = this.description,
            icon = this.icon,
            temp = this.temp,
            feels_like = this.feels_like,
            pressure = this.pressure,
            humidity = this.humidity,
            temp_min = this.temp_min,
            temp_max = this.temp_max,
            speed = this.speed,
            deg = this.deg,
            all = this.all,
            //  type = this.sys.type,
            type = 0,
            country = this.country,
            sunrise = this.sunrise,
            sunset = this.sunset
        )
    }
}


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



sealed class AsyncWeatherInfoState {
    data class Success(val data:CURRENTWEATHER_TBL):AsyncWeatherInfoState()
    data class Error(val message:String) :AsyncWeatherInfoState()
    object  Loading:AsyncWeatherInfoState()

    object  Empty:AsyncWeatherInfoState()
}