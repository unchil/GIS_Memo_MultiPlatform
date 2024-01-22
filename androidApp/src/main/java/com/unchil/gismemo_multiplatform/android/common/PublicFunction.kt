package com.unchil.gismemo_multiplatform.android.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.size.Size
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.model.BiometricCheckObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
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

fun Context.getLanguageArray():Array<String>{
    return resources.getStringArray(R.array.Language_Array)
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChkNetWork(
    onCheckState:()->Unit
){
    val context = LocalContext.current
    val permissions = listOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)
    var isGranted by mutableStateOf(true)

    permissions.forEach { chkPermission ->
        isGranted = isGranted && multiplePermissionsState.permissions.find {
            it.permission == chkPermission
        }?.status?.isGranted ?: false
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                text = "Gis Momo"
            )

            Image(
                painter =  painterResource(R.drawable.baseline_wifi_off_black_48),
                modifier = Modifier
                    .clip(ShapeDefaults.Medium)
                    .width(160.dp)
                    .height(160.dp),
                contentDescription = "not Connected",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )

            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                onClick = {
                    onCheckState()
                }
            ) {
                Text(context.resources.getString(R.string.chkNetWork_msg))
            }
        }

    }
}




@Composable
fun Context.dpToSize(width: Dp, height:Dp):Size {
    return Size(
        ( width * this.resources.displayMetrics.density).value.toInt(),
        ( height * this.resources.displayMetrics.density).value.toInt()
    )
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

fun launchIntent_ShareMemo(context: Context, repository: GisMemoRepository, memo: MEMO_TBL){

    val FILEPROVIDER_AUTHORITY = "com.unchil.gismemo_multiplatform.fileprovider"

    // val repository = RepositoryProvider.getRepository(context.applicationContext)
    repository.getShareMemoData(id = memo.id) { attachment, comments ->

        val attachmentUri = arrayListOf<Uri>()

        attachment.forEach {
            attachmentUri.add(
                FileProvider.getUriForFile(  context,
                    FILEPROVIDER_AUTHORITY,  File(it)  )
            )
        }

        val subject =  memo.title
        var text = "${memo.desc} \n${memo.snippets} \n\n"

        comments.forEachIndexed { index, comment ->
            text = text + "[${index}]: ${comment}" + "\n"
        }

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"

            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, subject)

            putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUri)
        }
        context.startActivity(intent)

    }

}


fun biometricPrompt(
    context: Context,
    bioMetricCheckType: BiometricCheckObject.Type,
    onResult: (isSucceeded:Boolean, bioMetricCheckType: BiometricCheckObject.Type, errorMsg:String?  ) ->Unit
){


    val biometricPrompt = BiometricPrompt.Builder(context)
        .apply {
            setTitle(BiometricCheckObject.getTitle(bioMetricCheckType, context.resources::getString).first)
            setSubtitle(BiometricCheckObject.getTitle(bioMetricCheckType, context.resources::getString).second)
            setDescription(context.resources.getString(R.string.biometric_desc))
            //BiometricPrompt.PromptInfo.Builder 인스턴스에서는 setNegativeButtonText()와 setAllowedAuthenticators(... or DEVICE_CREDENTIAL)를 동시에 호출할 수 없습니다.
            setAllowedAuthenticators( BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            //   setNegativeButton("취소", context.mainExecutor, { _ , _ ->   })

        }.build()

    biometricPrompt.authenticate(android.os.CancellationSignal(), context.mainExecutor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onResult(false, bioMetricCheckType,  errString.toString())
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onResult(false, bioMetricCheckType, context.resources.getString(R.string.biometric_err_msg))
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onResult(true, bioMetricCheckType, null)
            }
        }
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetDragHandler(coroutineScope: CoroutineScope, scaffoldState: BottomSheetScaffoldState){
    Box(
        modifier = Modifier.height(30.dp),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            modifier = Modifier
                .scale(1f)
                .clickable {
                    coroutineScope.launch {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Hidden
                            || scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
                        ) {
                            scaffoldState.bottomSheetState.expand()
                        } else {
                            scaffoldState.bottomSheetState.hide()
                        }
                    }
                },
            imageVector = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded)
                Icons.Outlined.KeyboardArrowDown  else Icons.Outlined.KeyboardArrowUp,
            contentDescription = "search",
        )
    }
}
