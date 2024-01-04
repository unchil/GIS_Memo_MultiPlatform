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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory
import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.RecvWeatherDataState
import com.unchil.gismemo.shared.composables.LocalPermissionsManager
import com.unchil.gismemo.shared.composables.PermissionsManager
import com.unchil.gismemo_multiplatform.android.ChkNetWork
import com.unchil.gismemo_multiplatform.android.MyApplicationTheme
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
import com.unchil.gismemo_multiplatform.android.viewModel.WeatherViewModel
import kotlinx.coroutines.delay
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
        isGranted =   isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission  }?.status?.isGranted ?: false
    }


    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions ,
        viewType = PermissionRequiredComposeFuncName.Weather
    ) {


        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val coroutineScope = rememberCoroutineScope()

        val viewModel = remember {
            WeatherViewModel(repository = GisMemoRepository(DatabaseDriverFactory(context = context))  )
        }

        val fusedLocationProviderClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }


        var isSuccessfulTask by remember { mutableStateOf( false ) }
        var checkCurrentLocation by remember { mutableStateOf(true) }

        var isConnect  by  remember { mutableStateOf(context.checkInternetConnected()) }

        LaunchedEffect(key1 = isConnect ){
            while(!isConnect) {
                delay(500)
                isConnect = context.checkInternetConnected()

                if(isConnect && !checkCurrentLocation){
                    checkCurrentLocation = true
                }
            }
        }


        isConnect  = context.checkInternetConnected()

        LaunchedEffect(key1 =  checkCurrentLocation, key2 = isConnect){
            if(checkCurrentLocation) {
                checkCurrentLocation = false
                fusedLocationProviderClient.lastLocation.addOnCompleteListener( context.mainExecutor) { task ->
                    if (task.isSuccessful && task.result != null ) {
                        isSuccessfulTask = true

                        if(isConnect) {
                            viewModel.onEvent(WeatherViewModel.Event.SearchWeather(task.result.toLatLngAlt()))
                        }

                    } else {
                        onCheckLocationService?.let {
                            it(false)
                        }
                    }
                }
            }
        }

        val resultState = viewModel.currentWeatheStaterFlow.collectAsState()

        Column(
            modifier = Modifier
                .clip(shape = ShapeDefaults.ExtraSmall)
                .clickable(false, null, null) {}
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surfaceVariant)

            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when(resultState.value){
                is RecvWeatherDataState.Error -> {
                    val result = (resultState.value as RecvWeatherDataState.Error)
                    Box( modifier = Modifier
                        .fillMaxSize()
                    ) {
                        Text(
                            text = result.message,
                            modifier = Modifier
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }

                }
                RecvWeatherDataState.Loading -> {

                    Box( modifier = Modifier
                        .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material.CircularProgressIndicator( )
                    }


                }
                is RecvWeatherDataState.Success -> {
                    val result = (resultState.value as RecvWeatherDataState.Success)
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
            }

            AnimatedVisibility  (!isSuccessfulTask  ) {
                IconButton(
                    onClick = { checkCurrentLocation = true },
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

            if (!isConnect) {
                ChkNetWork(
                    onCheckState = {
                        coroutineScope.launch {
                            isConnect =  context.checkInternetConnected()
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

    val permissionsManager = PermissionsManager()

    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {

                WeatherContent()


            }


        }
    }
}