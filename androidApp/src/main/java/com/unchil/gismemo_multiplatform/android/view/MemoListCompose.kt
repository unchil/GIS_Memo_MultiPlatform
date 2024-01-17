package com.unchil.gismemo_multiplatform.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.theme.MyApplicationTheme
import com.unchil.gismemo_multiplatform.android.viewModel.MemoListViewModel
import com.unchil.gismemo_multiplatform.android.viewModel.WriteMemoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MemoListCompose(){

    val context = LocalContext.current
    val repository = LocalRepository.current
    val viewModel = remember { MemoListViewModel( repository = repository ) }

    val isRefreshing: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            // Send Refresh Event
        }
    )

    val lazyListState = rememberLazyListState()

    val upButtonPadding  = remember {  mutableStateOf ( 10.dp ) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pullRefresh(state = pullRefreshState)
    ) {

        LazyColumn(
            modifier = Modifier,
            state = lazyListState,
            userScrollEnabled = true,
            verticalArrangement = Arrangement.SpaceBetween,
            contentPadding = PaddingValues(
                horizontal = 2.dp,
                vertical = 2.dp
            )
        ) {


        }

        PullRefreshIndicator(
            refreshing = isRefreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        UpButton(
            modifier = Modifier
                .padding(end = 10.dp, bottom = upButtonPadding.value)
                .align(Alignment.BottomEnd),
            listState = lazyListState
        )

    }

}


@Composable
fun UpButton(
    modifier:Modifier,
    listState: LazyListState
){

    val showButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    val coroutineScope = rememberCoroutineScope()
    /*
        val isUsableHaptic = LocalUsableHaptic.current
        val hapticFeedback = LocalHapticFeedback.current
        fun hapticProcessing(){
            if(isUsableHaptic){
                coroutineScope.launch {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
        }
     */
    if( showButton) {
        FloatingActionButton(
            modifier = Modifier.then(modifier),
            elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp),
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                    //      hapticProcessing()
                }
            }
        ) {
            Icon(
                modifier = Modifier.scale(1f),
                imageVector = Icons.Outlined.Publish,
                contentDescription = "Up"
            )
        }
    }
}


@Preview
@Composable
fun PrevMemoListCompose() {
    val context = LocalContext.current
    val permissionsManager = PermissionsManager()
    val navController = rememberNavController()
    val repository = PlatformObject.getRepository(context)
    CompositionLocalProvider(
        LocalPermissionsManager provides permissionsManager,
        LocalRepository provides repository
    ) {
        MyApplicationTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MemoListCompose()
            }
        }
    }

}
