package com.unchil.gismemo_multiplatform.android

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.unchil.gismemo_multiplatform.android.common.ChkNetWork
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
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
                    gridWidth.floatValue = 1f
                }
                else ->{
                    isPortrait.value = false
                    gridWidth.floatValue = 0.9f
                }
            }


            LaunchedEffect(key1 = currentBackStack){
                val currentScreen = MainTabObject.Types.find {
                    it.route ==  currentBackStack?.destination?.route
                }
                selectedItem.intValue =  MainTabObject.Types.indexOf(currentScreen)
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
                                                    selected = selectedItem.intValue == index,
                                                    onClick = {
                                                        isPressed.value = true
                                                        selectedItem.intValue = index
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
                                                modifier = Modifier
                                                    .shadow(elevation = 1.dp)
                                                    .width(80.dp),
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            ) {

                                                Spacer(Modifier.padding( vertical = 20.dp) )

                                                MainTabObject.Types.forEachIndexed { index, gisMemoDestinations ->
                                                    NavigationRailItem(
                                                        icon = {
                                                            Icon(
                                                                imageVector = gisMemoDestinations.icon ?: Icons.Outlined.Info,
                                                                contentDescription = context.resources.getString( gisMemoDestinations.name),
                                                                tint = if (selectedItem.intValue == index) Color.Red else MaterialTheme.colorScheme.secondary
                                                            )
                                                        },
                                                        label = {
                                                            Text(
                                                                text = context.resources.getString( gisMemoDestinations.name ),
                                                                color = if (selectedItem.intValue == index) Color.Red else MaterialTheme.colorScheme.secondary
                                                            )
                                                        },
                                                        selected = selectedItem.intValue == index,
                                                        onClick = {
                                                            isPressed.value = true
                                                            selectedItem.intValue = index
                                                            navController.navigateTo(MainTabObject.Types[index].route )
                                                        }
                                                    )
                                                }

                                                Spacer(Modifier.padding( vertical = 20.dp) )
                                            }
                                        }

                                        Box( modifier = Modifier.fillMaxWidth(gridWidth.floatValue )) {
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


