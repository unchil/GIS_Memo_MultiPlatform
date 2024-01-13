package com.unchil.gismemo_multiplatform.android.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.HighlightOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.size.Size
import com.jetbrains.handson.kmm.shared.data.WriteMemoData
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.model.MemoData
import com.unchil.gismemo_multiplatform.android.model.MemoDataUser
import com.unchil.gismemo_multiplatform.android.model.SnackBarChannelObject
import com.unchil.gismemo_multiplatform.android.model.WriteMemoDataDesc
import com.unchil.gismemo_multiplatform.android.viewModel.MemoDataViewModel
import com.unchil.gismemo_multiplatform.android.viewModel.WriteMemoViewModel
import kotlinx.coroutines.channels.Channel

@SuppressLint("UnrememberedMutableState")
@Composable
fun MemoDataCompose(
    onEvent:((WriteMemoViewModel.Event)->Unit)? = null,
    deleteHandle:((index:Int)->Unit)? = null,
    channel: Channel<Int>? = null
){

    val context = LocalContext.current
    val repository = LocalRepository.current


    val viewModel = remember {
        MemoDataViewModel(
            repository = repository,
            user =  if (onEvent == null )
                MemoDataUser.DetailMemoView else MemoDataUser.WriteMemoView
        )
    }

    val coroutineScope = rememberCoroutineScope()


    val currentTabView = remember {
        mutableStateOf(WriteMemoData.Type.SNAPSHOT)
    }

    val currentTabIndex =  remember {
        mutableIntStateOf(0)
    }

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor =  MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
    ) {


        WriteMemoData.Types.forEachIndexed { index, type ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = WriteMemoDataDesc(type).second,
                        contentDescription = context.resources.getString(   WriteMemoDataDesc(type).first )
                    )
                },
                label = { Text( context.resources.getString(   WriteMemoDataDesc(type).first) ) },
                selected = currentTabView.value ==  type,
                onClick = {
                    //    hapticProcessing()
                    currentTabView.value = type
                    currentTabIndex.value = index
                },
            )
        }

    }

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        repeat(WriteMemoData.Types.size) { iteration ->
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(1f / (WriteMemoData.Types.size - iteration))
                    .clip(CircleShape)
                    .background(
                        if (currentTabIndex.value == iteration)
                            Color.Red
                        else Color.Transparent
                    )
            )
        }
    }


    val memoData: MutableState<MemoData?> = mutableStateOf(
        when (currentTabView.value){
            WriteMemoData.Type.PHOTO ->  {
                MemoData.Photo(
                    dataList = viewModel.photoListStateFlow.collectAsState().value.toMutableList()
                )
            }
            WriteMemoData.Type.AUDIOTEXT -> {
                MemoData.AudioText(
                    dataList = viewModel.audioTextStateFlow.collectAsState().value.toMutableList()
                )
            }
            WriteMemoData.Type.VIDEO ->  {
                MemoData.Video(
                    dataList = viewModel.videoListStateFlow.collectAsState().value.toMutableList()
                )
            }
            WriteMemoData.Type.SNAPSHOT ->  {
                MemoData.SnapShot(
                    dataList = viewModel.snapShotListStateFlow.collectAsState().value.toMutableList()
                )
            }

        }
    )

    val onDelete:((page:Int) -> Unit)  =   { page ->
        if (currentTabView.value ==  WriteMemoData.Type.SNAPSHOT ) {
            deleteHandle?.let {
                it (page)
            }
        }
        onEvent?.let {
            it(WriteMemoViewModel.Event.DeleteMemoItem(currentTabView.value, page))
        }
    }

    Column(
        modifier = Modifier
    ) {
        memoData.value?.let {
            when (it) {
                is MemoData.AudioText -> {
                    PagerAudioTextView(
                        item = it,
                        onDelete = if(deleteHandle != null) onDelete else null,
                        channel = channel
                    )
                }
                is MemoData.Photo -> {
                    PagerPhotoView(
                        item = it,
                        onDelete = if(deleteHandle != null) onDelete else null,
                        channel = channel
                    )
                }
                is MemoData.SnapShot -> {
                    PagerSnapShotView(
                        item = it,
                        onDelete = if(deleteHandle != null) onDelete else null,
                        channel = channel
                    )
                }
                is MemoData.Video -> {
                    PagerVideoView(
                        item = it,
                        onDelete = if(deleteHandle != null) onDelete else null,
                        channel = channel
                    )
                }
            }
        }
    }


}


@Composable
fun AudioTextView(data: Pair<String, List<String>>){

   // val isUsableHaptic = LocalUsableHaptic.current
  //  val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
/*
    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

 */

    var speechInput =  rememberSaveable { data.first }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        horizontalAlignment= Alignment.CenterHorizontally
    ) {


        OutlinedTextField(
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),

            singleLine = false,
            trailingIcon = {
                IconButton(
                    onClick = {
                  //      hapticProcessing()
                        speechInput = ""
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.HighlightOff,
                        contentDescription = "Clear"
                    )
                }
            },
            value = speechInput,
            onValueChange = { speechInput = it },
            label = { Text("Speech To Text") },
            shape = OutlinedTextFieldDefaults.shape,
            keyboardActions = KeyboardActions.Default
        )

    }

}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerAudioTextView(
    item: MemoData.AudioText,
    onDelete:((page:Int) -> Unit)? = null,
    channel:Channel<Int>? = null
){
    val context = LocalContext.current
  //  val isUsableHaptic = LocalUsableHaptic.current
  //  val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    /*
    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

     */


    val defaultData:Pair<String, Int> = Pair(
        context.resources.getString(WriteMemoDataDesc(WriteMemoData.Type.AUDIOTEXT).first )
        , item.dataList.size)

    val pagerState  =   rememberPagerState(
        initialPage = 0,
        pageCount = { item.dataList.count() }
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(2.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            androidx.compose.material3.Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center,
                text = defaultData.first,
                style = MaterialTheme.typography.titleMedium
            )

            onDelete?.let {
                if (defaultData.second > 0) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                         //   hapticProcessing()
                            it(pagerState.currentPage)
                            channel?.let { channel ->
                                channel.trySend(SnackBarChannelObject.entries.first { channelInfo ->
                                    channelInfo.channelType == SnackBarChannelObject.Type.ITEM_DELETE
                                }.channel)
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    )
                }
            }

        }

        Row(
            modifier = Modifier.padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(defaultData.second) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    Color.Red else  MaterialTheme.colorScheme.onSurface
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }

        if (defaultData.second > 0) {
            HorizontalPager(
                modifier = Modifier.padding(horizontal = 0.dp)
                    .verticalScroll(state = scrollState)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
              //  pageCount = defaultData.second,
                state = pagerState,
            ) { page ->

                AudioTextView(data = item.dataList[page])

            }

            Box(
                modifier = Modifier
                    .height(260.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                ,
                contentAlignment = Alignment.Center

            ) {

                ExoplayerCompose(
                    uriList = item.dataList[pagerState.targetPage].second
                )
            }

        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(color = Color.DarkGray)
            )
        }

    } // Column

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerPhotoView(
    item: MemoData.Photo,
    onDelete:((page:Int) -> Unit)? = null,
    channel:Channel<Int>? = null
){
    val context = LocalContext.current
   // val isUsableHaptic = LocalUsableHaptic.current
 //   val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    /*
    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

     */

    val pagerState  =   rememberPagerState(
        initialPage = 0,
        pageCount = { item.dataList.count() }
    )

    val defaultData:Pair<String, Int> = Pair(
        context.resources.getString(WriteMemoDataDesc(WriteMemoData.Type.PHOTO).first )
        , item.dataList.size)

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(2.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            androidx.compose.material3.Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center,
                text = defaultData.first,
                style = MaterialTheme.typography.titleMedium
            )

            onDelete?.let {
                if (defaultData.second > 0) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                         //   hapticProcessing()
                            it(pagerState.currentPage)
                            channel?.let { channel ->
                                channel.trySend(SnackBarChannelObject.entries.first { channelInfo ->
                                    channelInfo.channelType == SnackBarChannelObject.Type.ITEM_DELETE
                                }.channel)
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    )
                }
            }

        }

        Row(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(defaultData.second) { iteration ->
                val color =   if (pagerState.currentPage == iteration)
                    Color.Red else  MaterialTheme.colorScheme.onSurface
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }


        if (defaultData.second > 0) {
            HorizontalPager(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .verticalScroll(state = scrollState)
                    .fillMaxSize()
                    .background(color = Color.White),
               // pageCount = defaultData.second,
                state = pagerState,
            ) { page ->
                ImageViewer(
                    data = (item.dataList[page]),
                    size = Size.ORIGINAL,
                    isZoomable = false
                )
            }

        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(color = Color.DarkGray)
            )
        }

    } // Column

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerSnapShotView(
    item: MemoData.SnapShot,
    onDelete:((page:Int) -> Unit)? = null,
    channel:Channel<Int>? = null
){

    val context = LocalContext.current
 //   val isUsableHaptic = LocalUsableHaptic.current
 //   val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
/*
    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

 */
    val pagerState  =   rememberPagerState(
        initialPage = 0,
        pageCount = { item.dataList.count() }
    )

    val defaultData:Pair<String, Int> =  Pair(
        context.resources.getString(WriteMemoDataDesc(WriteMemoData.Type.SNAPSHOT).first )
        , item.dataList.size)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(2.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            androidx.compose.material3.Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center,
                text = defaultData.first,
                style = MaterialTheme.typography.titleMedium
            )

            onDelete?.let { deleteHandler ->
                if (defaultData.second > 0) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                   //         hapticProcessing()
                            deleteHandler(pagerState.currentPage)
                            channel?.let { channel ->
                                channel.trySend(SnackBarChannelObject.entries.first { channelInfo->
                                    channelInfo.channelType == SnackBarChannelObject.Type.ITEM_DELETE
                                }.channel)
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    )
                }
            }

        }

        Row(
            modifier = Modifier.padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(defaultData.second) { iteration ->
                val color =if (pagerState.currentPage == iteration)
                    Color.Red else MaterialTheme.colorScheme.onSurface
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }

        if (defaultData.second > 0) {
            HorizontalPager(
                modifier = Modifier .padding(10.dp)
                    .verticalScroll(state = scrollState)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
            //    pageCount = defaultData.second,
                state = pagerState,
            ) { page ->
                ImageViewer(
                    data = (item.dataList[page]),
                    size = Size.ORIGINAL,
                    isZoomable = false
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(color = Color.DarkGray)
            )
        }

    } // Column

}

@Composable
fun PagerVideoView(
    item: MemoData.Video,
    onDelete:((page:Int) -> Unit)? = null,
    channel:Channel<Int>? = null
){
    val context = LocalContext.current
 //   val isUsableHaptic = LocalUsableHaptic.current
 //   val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    /*
    fun hapticProcessing(){
        if(isUsableHaptic){
            coroutineScope.launch {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

     */

    var videoTrackIndex by remember { mutableIntStateOf(0) }
    val defaultData:Pair<String, Int> = Pair(
        context.resources.getString(WriteMemoDataDesc(WriteMemoData.Type.VIDEO).first )
        , item.dataList.size)

    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(2.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            androidx.compose.material3.Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(color = Color.Transparent)
                    .padding(top = 10.dp),
                textAlign = TextAlign.Center,
                text = defaultData.first,
                style = MaterialTheme.typography.titleMedium
            )

            onDelete?.let { deleteHandler ->
                if (defaultData.second > 0) {
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                          //  hapticProcessing()
                            deleteHandler(videoTrackIndex)
                            channel?.let { channel ->
                                channel.trySend(SnackBarChannelObject.entries.first { channelInfo ->
                                    channelInfo.channelType == SnackBarChannelObject.Type.ITEM_DELETE
                                }.channel)
                            }
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    )
                }
            }

        }

        Row(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(defaultData.second) { iteration ->

                val color =   if (videoTrackIndex == iteration)
                    Color.Red else  MaterialTheme.colorScheme.onSurface
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }


        // .verticalScroll(state = scrollState) 사용시 ExoplayerCompose minimum size 가 된다.

        val draggableState =  rememberDraggableState{}

        if (defaultData.second > 0) {

            Box(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        onDragStopped = {
                            videoTrackIndex =
                                if (it >= 0F) {
                                    if (videoTrackIndex - 1 > 0)
                                        --videoTrackIndex
                                    else
                                        0
                                } else {
                                    if (videoTrackIndex < defaultData.second - 1)
                                        ++videoTrackIndex
                                    else
                                        defaultData.second - 1
                                }
                        }
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                ExoplayerCompose(
                    uriList = item.dataList,
                    setTrackIndex = {exoPlayer ->
                        exoPlayer.seekTo(videoTrackIndex, 0)
                    }
                ) {
                    videoTrackIndex = it
                }
            }

        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .background(color = Color.DarkGray)
            )
        }


    } // Column


}
