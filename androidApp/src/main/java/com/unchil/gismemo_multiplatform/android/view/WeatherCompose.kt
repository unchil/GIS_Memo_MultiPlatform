package com.unchil.gismemo.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.jetbrains.handson.kmm.shared.entity.AsyncWeatherInfoState
import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.common.ChkNetWork
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.LocalUsableHaptic
import com.unchil.gismemo_multiplatform.android.theme.GisMemoTheme
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName
import com.unchil.gismemo_multiplatform.android.common.checkInternetConnected
import com.unchil.gismemo_multiplatform.android.common.toLatLngAlt
import com.unchil.gismemo_multiplatform.android.common.toTextHeadLine
import com.unchil.gismemo_multiplatform.android.common.toTextSun
import com.unchil.gismemo_multiplatform.android.common.toTextTemp
import com.unchil.gismemo_multiplatform.android.common.toTextWeather
import com.unchil.gismemo_multiplatform.android.common.toTextWeatherDesc
import com.unchil.gismemo_multiplatform.android.common.toTextWind
import com.unchil.gismemo_multiplatform.android.view.hapticProcessing
import com.unchil.gismemo_multiplatform.android.viewModel.WeatherViewModel
import kotlinx.coroutines.launch


private fun getWeatherIcon(type:String):Int {
    return when(type){
        "01d" -> R.drawable.ic_openweather_01d
        "01n" -> R.drawable.ic_openweather_01n
        "02d" -> R.drawable.ic_openweather_02d
        "02n" -> R.drawable.ic_openweather_02n
        "03d" -> R.drawable.ic_openweather_03d
        "03n" -> R.drawable.ic_openweather_03n
        "04d" -> R.drawable.ic_openweather_04d
        "04n" -> R.drawable.ic_openweather_04n
        "09d" -> R.drawable.ic_openweather_09d
        "09n" -> R.drawable.ic_openweather_09n
        "10d" -> R.drawable.ic_openweather_10d
        "10n" -> R.drawable.ic_openweather_10n
        "11d" -> R.drawable.ic_openweather_11d
        "11n" -> R.drawable.ic_openweather_11n
        "13d" -> R.drawable.ic_openweather_13d
        "13n" -> R.drawable.ic_openweather_13n
        "50d" -> R.drawable.ic_openweather_50d
        "50n" -> R.drawable.ic_openweather_50n
        else -> R.drawable.ic_openweather_unknown
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission", "SuspiciousIndentation", "UnrememberedMutableState")
@Composable
fun WeatherContent(isSticky:Boolean = false , onCheckLocationService:((Boolean)->Unit)? = null){

    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val multiplePermissionsState = rememberMultiplePermissionsState( permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)

    var isGranted by mutableStateOf(true)
    permissions.forEach { chkPermission ->
        isGranted =   isGranted && multiplePermissionsState.permissions.find {
            it.permission == chkPermission
        }?.status?.isGranted ?: false
    }


    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions ,
        viewType = PermissionRequiredComposeFuncName.Weather
    ) {


        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val coroutineScope = rememberCoroutineScope()
        val isUsableHaptic = LocalUsableHaptic.current
        val hapticFeedback = LocalHapticFeedback.current
        val repository = LocalRepository.current
        val viewModel = remember {  WeatherViewModel(repository = repository ) }

        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }

        var isSetLocation by remember { mutableStateOf( false ) }

        var isConnected by remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnected, key2 = isSetLocation){
            if(isConnected && !isSetLocation ) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener( context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null ) {
                        isSetLocation = true
                        viewModel.onEvent(
                            WeatherViewModel.Event.SearchWeather(task.result.toLatLngAlt())
                        )
                    }
                }
            }
        }

        val resultState = viewModel.currentWeatherStateFlow.collectAsState()

        Column(
            modifier = Modifier
                .clip(shape = ShapeDefaults.ExtraSmall)
                .clickable(false, null, null) {}
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceVariant) ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            when(resultState.value){
                is AsyncWeatherInfoState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {

                        Image(
                            painterResource(R.drawable.weathercontent),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )
                        val msg = (resultState.value as AsyncWeatherInfoState.Error).message

                        Text(
                            text = msg,
                            color = Color.Red,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(color = Color.DarkGray.copy(alpha = 0.3f))
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                AsyncWeatherInfoState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {

                        Image(
                            painterResource(R.drawable.weathercontent),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = context.getString(R.string.weather_load_empty),
                            color = Color.Red,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(color = Color.DarkGray.copy(alpha = 0.8f))
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )

                    }
                }
                AsyncWeatherInfoState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 60.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        androidx.compose.material.CircularProgressIndicator( )
                    }
                }

                is AsyncWeatherInfoState.Success -> {
                    val result = (resultState.value as AsyncWeatherInfoState.Success)
                    AnimatedVisibility(true) {
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_PORTRAIT -> {
                                WeatherView(result.data)
                            }

                            else -> {
                                if (isSticky) {
                                    WeatherView(result.data)
                                } else {
                                    WeatherViewLandScape(result.data)
                                }
                            }
                        }
                    }
                }

                else -> {}
            }

            if(!isSetLocation) {
                IconButton(
                    onClick = {
                        isSetLocation = false
                        hapticProcessing(coroutineScope ,hapticFeedback, isUsableHaptic)
                    },
                    content = {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.padding(end = 10.dp),
                                imageVector = Icons.Outlined.LocationSearching,
                                contentDescription = "LocationSearching"
                            )
                            Text(text = context.resources.getString(R.string.weather_location_searching))
                        }
                    })
            }


            if (!isConnected) {
                ChkNetWork(
                    onCheckState = {
                        coroutineScope.launch {
                            isConnected =  context.checkInternetConnected()
                        }
                    }
                )
            }

        }

    }


}

@Composable
fun WeatherView(
    item: CURRENTWEATHER_TBL,
    modifier:Modifier = Modifier
){

    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text =  item.toTextHeadLine()
            , modifier = Modifier.fillMaxWidth()
            , textAlign = TextAlign.Center
            , style  = MaterialTheme.typography.titleSmall
        )

        Text(item.toTextWeatherDesc()
            , modifier = Modifier.fillMaxWidth()
            , textAlign = TextAlign.Center
            , style  = MaterialTheme.typography.titleSmall
        )

        Row(
            modifier = Modifier.align(Alignment.Start)
        ) {
            Image(
                painter =  painterResource(id = getWeatherIcon(item.icon)),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clip(ShapeDefaults.Small)
                    .fillMaxWidth(0.2f),
                contentDescription = "weather",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )

            Column (modifier = Modifier.padding(start= 10.dp)){
                WeatherItem(id =  Icons.Outlined.WbTwilight, desc = item.toTextSun(context.resources::getString))
                WeatherItem(id = Icons.Outlined.DeviceThermostat, desc = item.toTextTemp(context.resources::getString))
                WeatherItem(id = Icons.Outlined.WindPower, desc = item.toTextWind(context.resources::getString))
                WeatherItem(id = Icons.Outlined.Storm, desc = item.toTextWeather(context.resources::getString))
            }
        }

    }

}

@Composable
fun WeatherViewLandScape(
    item: CURRENTWEATHER_TBL,
    modifier:Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            item.toTextHeadLine(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )

       Text(
            item.toTextWeatherDesc(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )


        Image(
            painter =  painterResource(id = getWeatherIcon(item.icon)),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .clip(ShapeDefaults.Small)
                .fillMaxWidth(0.5f),
            contentDescription = "weather",
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )



        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalAlignment = Alignment.Start
        ){
            WeatherItem(id =  Icons.Outlined.WbTwilight, desc = item.toTextSun(context.resources::getString))
            WeatherItem(id = Icons.Outlined.DeviceThermostat, desc = item.toTextTemp(context.resources::getString))
            WeatherItem(id = Icons.Outlined.WindPower, desc = item.toTextWind(context.resources::getString))
            WeatherItem(id = Icons.Outlined.Storm, desc = item.toTextWeather(context.resources::getString))
        }


    }

}

@Composable
fun WeatherItem(id: ImageVector, desc: String){

    Row( modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {

        Icon(  imageVector = id
            , contentDescription = "desc"
            , modifier = Modifier
                .height(20.dp)
                .padding(end = 10.dp)

        )

        Text( desc
            , modifier = Modifier
            , textAlign = TextAlign.Start
            , style  = MaterialTheme.typography.bodySmall
        )
    }


}

@Preview
@Composable
fun PrevViewWeather(){

    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val repository = PlatformObject.getRepository(context)
    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {
        GisMemoTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                WeatherContent()
            }
        }
    }
}