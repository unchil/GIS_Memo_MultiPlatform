package com.unchil.gismemo_multiplatform.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.unchil.gismemo_multiplatform.android.common.CheckPermission
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionRequiredCompose
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.common.checkInternetConnected
import com.unchil.gismemo_multiplatform.android.model.MainTabObject
import com.unchil.gismemo_multiplatform.android.navigation.navigateTo
import com.unchil.gismemo_multiplatform.android.theme.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.view.GisMemoNavHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val LocalRepository = compositionLocalOf<GisMemoRepository> { error("No GisMemoRepository found!") }


class MainActivity : ComponentActivity() {

    private val permissionsManager = PermissionsManager()
    private val repository = GisMemoApp.repository!!

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /*
            val url = DestinationsLocalDataSource.craneDestinations.find {
                it.city.equals("GRANADA")
            }?.imageUrl ?: ""

            val uriList = listOf<Uri>(
                "/data/data/com.unchil.gismemo_multiplatform.android/files/videos/test.mp4".toUri()
            )

             */
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val navController = rememberAnimatedNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val configuration = LocalConfiguration.current

            val selectedItem = rememberSaveable { mutableIntStateOf(0) }
            val isPressed = remember { mutableStateOf(false) }

            val isPortrait = remember { mutableStateOf(false) }
            val gridWidth = remember { mutableFloatStateOf(1f) }



            when (configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    isPortrait.value = true
                    gridWidth.value = 1f
                }
                else ->{
                    isPortrait.value = false
                    gridWidth.value = 0.9f
                }
            }


            LaunchedEffect(key1 = currentBackStack){
                val currentScreen = MainTabObject.Types.find {
                    it.route ==  currentBackStack?.destination?.route
                }
                selectedItem.value =  MainTabObject.Types.indexOf(currentScreen)
            }

            val isConnect  = remember { mutableStateOf(context.checkInternetConnected()) }

            LaunchedEffect(key1 = isConnect.value ){
                while(!isConnect.value) {
                    delay(500)
                    isConnect.value = context.checkInternetConnected()
                }
            }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalPermissionsManager provides permissionsManager,
                        LocalRepository provides repository,
                    ){
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (!isConnect.value) {
                                ChkNetWork(
                                    onCheckState = {
                                        coroutineScope.launch {
                                            isConnect.value = checkInternetConnected()
                                        }
                                    }
                                )
                            } else {

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ){
                                    if (isPortrait.value) {
                                        BottomNavigation(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp)
                                                .shadow(elevation = 1.dp),
                                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                        ) {

                                            Spacer(Modifier.padding( horizontal = 10.dp ))

                                            MainTabObject.Types.forEachIndexed { index, gisMemoDestinations ->
                                                BottomNavigationItem(
                                                    icon = {
                                                        Icon(
                                                            imageVector = gisMemoDestinations.icon ?: Icons.Outlined.Info,
                                                            contentDescription = context.resources.getString(gisMemoDestinations.name),
                                                            tint = if (selectedItem.intValue == index) MaterialTheme.colorScheme.scrim
                                                                    else MaterialTheme.colorScheme.outline
                                                        )
                                                    },
                                                    label = {
                                                        Text(
                                                            text = context.resources.getString(gisMemoDestinations.name ),
                                                            color  = if (selectedItem.intValue == index) MaterialTheme.colorScheme.scrim
                                                                        else MaterialTheme.colorScheme.outline
                                                        )
                                                    },
                                                    selected = selectedItem.value == index,
                                                    onClick = {
                                                        isPressed.value = true
                                                        selectedItem.value = index
                                                        navController.navigateTo(MainTabObject.Types[index].route )
                                                    },
                                                    selectedContentColor = Color.Red,
                                                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                                                )
                                            }

                                            Spacer(Modifier.padding( horizontal = 10.dp ))
                                        }
                                    }

                                    Row(
                                        modifier = Modifier,
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){

                                        if(!isPortrait.value){
                                            NavigationRail(
                                                modifier = Modifier.shadow(elevation = 1.dp ).width(80.dp),
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            ) {

                                                Spacer(Modifier.padding( vertical = 20.dp) )

                                                MainTabObject.Types.forEachIndexed { index, gisMemoDestinations ->
                                                    NavigationRailItem(
                                                        icon = {
                                                            Icon(
                                                                imageVector = gisMemoDestinations.icon ?: Icons.Outlined.Info,
                                                                contentDescription = context.resources.getString( gisMemoDestinations.name),
                                                                tint = if (selectedItem.value == index) Color.Red else MaterialTheme.colorScheme.secondary
                                                            )
                                                        },
                                                        label = {
                                                            Text(
                                                                text = context.resources.getString( gisMemoDestinations.name ),
                                                                color = if (selectedItem.value == index) Color.Red else MaterialTheme.colorScheme.secondary
                                                            )
                                                        },
                                                        selected = selectedItem.value == index,
                                                        onClick = {
                                                            isPressed.value = true
                                                            selectedItem.value = index
                                                            navController.navigateTo(MainTabObject.Types[index].route )
                                                        }
                                                    )
                                                }

                                                Spacer(Modifier.padding( vertical = 20.dp) )
                                            }
                                        }

                                        Box( modifier = Modifier.fillMaxWidth(gridWidth.value )) {
                                            GisMemoNavHost(navController = navController)
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
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

