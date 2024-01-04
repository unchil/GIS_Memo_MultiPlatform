@file:OptIn(ExperimentalMaterial3Api::class)

package com.unchil.gismemo_multiplatform.android.view

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.widgets.ScaleBar
import com.unchil.gismemo.shared.composables.LocalPermissionsManager
import com.unchil.gismemo.shared.composables.PermissionsManager
import com.unchil.gismemo_multiplatform.android.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredComposeFuncName

fun Location.toLatLng():LatLng{
    return LatLng(this.latitude, this.longitude)
}

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun GoogleMapView(){

    val permissions = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    CheckPermission(multiplePermissionsState = multiplePermissionsState)
    var isGranted by mutableStateOf(true)
    permissions.forEach { chkPermission ->
        isGranted =  isGranted && multiplePermissionsState.permissions.find { it.permission == chkPermission }?.status?.isGranted
                ?: false
    }




    val context = LocalContext.current

    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var currentLocation by remember {
        mutableStateOf(LatLng(0.0,0.0))
    }


    LaunchedEffect(key1 =  currentLocation){
        if( currentLocation == LatLng(0.0,0.0)) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener( context.mainExecutor) { task ->
                if (task.isSuccessful && task.result != null ) {
                    currentLocation = task.result.toLatLng()
                }
            }
        }
    }


    // No ~~~~ remember
    val markerState =  MarkerState( position = currentLocation )
    val defaultCameraPosition =  CameraPosition.fromLatLngZoom( currentLocation, 16f)
    var cameraPositionState =  CameraPositionState(position = defaultCameraPosition)

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true,
          //      mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle_night)
            )
        )
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                myLocationButtonEnabled = true,
                mapToolbarEnabled = true,
                zoomControlsEnabled = false

            )
        )
    }




    val onMapLongClickHandler: (LatLng) -> Unit = {
        markerState.position = it
        cameraPositionState = CameraPositionState( position =  CameraPosition.fromLatLngZoom(it, 16f))
    }

    PermissionRequiredCompose(
        isGranted = isGranted,
        multiplePermissions = permissions,
        viewType = PermissionRequiredComposeFuncName.Weather
    ) {


        Scaffold(
            modifier = Modifier,
        ) {

            Box(Modifier.fillMaxSize().padding(it)) {

                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    properties = mapProperties,
                    uiSettings = uiSettings,
                    onMapLongClick = onMapLongClickHandler,

                    ) {

                    Marker(
                        state = markerState,
                        title = "lat/lng:(${
                            String.format(
                                "%.5f",
                                markerState.position.latitude
                            )
                        },${String.format("%.5f", markerState.position.longitude)})",
                    )

                }

                ScaleBar(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .align(Alignment.BottomStart),
                    cameraPositionState = cameraPositionState
                )


            }


        }

    }

}


@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun PrevViewMap(){

     val permissionsManager = PermissionsManager()

    MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            CompositionLocalProvider(LocalPermissionsManager provides permissionsManager) {

                GoogleMapView()


            }


        }
    }
}