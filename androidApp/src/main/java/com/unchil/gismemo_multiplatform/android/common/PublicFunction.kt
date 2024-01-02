package com.unchil.gismemo_multiplatform.android.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.activity.ComponentActivity
import com.google.android.gms.maps.model.LatLng
import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
import com.unchil.gismemo_multiplatform.android.R
import java.text.SimpleDateFormat


const  val TAG_M_KM = 1000


const val MILLISEC_CHECK = 9999999999
const val MILLISEC_DIGIT = 1L
const val MILLISEC_CONV_DIGIT = 1000L
const val yyyyMMddHHmm = "yyyy/MM/dd HH:mm"
const val HHmmss = "HH:mm:ss"

enum class BiometricCheckType {
    DETAILVIEW, SHARE, DELETE
}

fun BiometricCheckType.getTitle(getString: (Int)->String):Pair<String,String> {
    return when(this){
        BiometricCheckType.DETAILVIEW  -> {
            Pair(getString(R.string.biometric_prompt_detailview_title), getString(R.string.biometric_prompt_detailview_msg))
        }
        BiometricCheckType.SHARE -> {
            Pair(getString(R.string.biometric_prompt_share_title), getString(R.string.biometric_prompt_share_msg))
        }
        BiometricCheckType.DELETE -> {
            Pair(getString(R.string.biometric_prompt_delete_title), getString(R.string.biometric_prompt_delete_msg))
        }
    }

}

@SuppressLint("SimpleDateFormat")
fun UnixTimeToString(time: Long, format: String): String{
    val UNIXTIMETAG_SECTOMILI
            = if( time > MILLISEC_CHECK) MILLISEC_DIGIT else MILLISEC_CONV_DIGIT

    return SimpleDateFormat(format)
        .format(time * UNIXTIMETAG_SECTOMILI )
        .toString()
}

fun CURRENTWEATHER_TBL.toTextHeadLine(): String {
    return UnixTimeToString(this.dt, yyyyMMddHHmm) + "  ${this.name}/${this.country}"
}


fun CURRENTWEATHER_TBL.toTextWeatherDesc(): String {
    return  "${this.main} : ${this.description}"
}


fun CURRENTWEATHER_TBL.toTextSun(getString: (Int)->String ): String {
    return String.format( getString(R.string.weather_desc_sun),
        UnixTimeToString(this.sunrise, HHmmss),
        UnixTimeToString(this.sunset, HHmmss)
    )
}

fun CURRENTWEATHER_TBL.toTextTemp(getString: (Int)->String): String {
    return String.format ( getString(R.string.weather_desc_temp),
        this.temp,
        this.temp_min,
        this.temp_max
    )
}


fun CURRENTWEATHER_TBL.toTextWeather(getString: (Int)->String): String {
    return String.format( getString(R.string.weather_desc_weather),
        this.pressure,
        this.humidity
    ) + "%"
}


fun CURRENTWEATHER_TBL.toTextWind(getString: (Int)->String): String {
    return   String.format(
        getString(R.string.weather_desc_wind),
        this.speed,
        this.deg,
        this.visibility/ TAG_M_KM )
}




fun Context.checkInternetConnected() :Boolean  {
    ( applicationContext.getSystemService(ComponentActivity.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
        activeNetwork?.let {network ->
            getNetworkCapabilities(network)?.let {networkCapabilities ->
                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> { false }
                }
            }
        }
        return false
    }
}


fun launchIntent_Biometric_Enroll(context: Context){
    val intent =   Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
        putExtra(
            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    }
    context.startActivity(intent)
}


fun Location.toLatLngAlt(): LatLngAlt {
    return LatLngAlt(
        collectTime= this.time,
        latitude = this.latitude.toFloat(),
        longitude = this.longitude.toFloat() ,
        altitude = this.altitude.toFloat()
    )
}
