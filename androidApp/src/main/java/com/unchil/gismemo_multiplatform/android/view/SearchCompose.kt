package com.unchil.gismemo_multiplatform.android.view

import android.annotation.SuppressLint
import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Class
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.unchil.gismemo_multiplatform.PlatformObject
import com.unchil.gismemo_multiplatform.android.LocalRepository
import com.unchil.gismemo_multiplatform.android.R
import com.unchil.gismemo_multiplatform.android.common.LocalPermissionsManager
import com.unchil.gismemo_multiplatform.android.common.PermissionsManager
import com.unchil.gismemo_multiplatform.android.model.RadioGroupOption
import com.unchil.gismemo_multiplatform.android.theme.GisMemoTheme

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompose(
    onMessage:(() -> Unit)? = null
){

    val context = LocalContext.current

    val focusmanager = LocalFocusManager.current

    var query_title by rememberSaveable {
        mutableStateOf("")
    }

    val historyItems = remember {
        mutableStateListOf<String>()
    }
    val scrollState = rememberScrollState()

    val isVisibleSearchBar: MutableState<Boolean> = remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState()

    val recognizerIntent = remember { recognizerIntent }

    val isTagBox = rememberSaveable{  mutableStateOf(false)}

    val isDateBox = rememberSaveable{  mutableStateOf(false)}

    val startLauncherRecognizerIntent = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val result =
                it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            query_title = query_title + result?.get(0).toString() + " "
        }
    }

    val onSearch : (String) -> Unit = {
        // call EventHandle
        if (it.isNotEmpty()) {
            historyItems.add(it)
        }
        isVisibleSearchBar.value = false
        focusmanager.clearFocus(true)
    }

    val placeholder: @Composable() (() -> Unit)? = {
        Text(
            text = "제목 검색",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    val leadingIcon: @Composable() (() -> Unit)? = {
        if (query_title.isNotEmpty()) {
            IconButton(
                modifier = Modifier,
                onClick = {
                  //  isHapticProcessing = true
                    query_title = ""
                    onMessage?.let {
                        it()
                    }
                },
                content = {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear"
                    )
                }
            )

        }
    }

    val trailingIcon: @Composable() (() -> Unit)? = {
        Row(
            modifier = Modifier.padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                modifier = Modifier,
                onClick = {
                //    isHapticProcessing = true
                    startLauncherRecognizerIntent.launch(recognizerIntent())
                },
                content = {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = "SpeechToText"
                    )
                }
            )

            IconButton(
                modifier = Modifier,
                onClick = {
                    // OnSearchEventHandler
                    if (query_title.isNotEmpty()) {
                        historyItems.add(query_title)
                    }
                    isVisibleSearchBar.value = false
                    focusmanager.clearFocus(true)

                },
                content = {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search"
                    )
                }
            )
        }

    }

    val hashTagBtn: @Composable() () -> Unit = {

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Class,
                contentDescription = "tag"
            )
            Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            Text(text = context.resources.getString(R.string.search_hashTag),
                style = MaterialTheme.typography.titleSmall)
            Icon(
                modifier = Modifier.scale(1f),
                imageVector = if (isTagBox.value)
                    Icons.Outlined.ArrowDropDown
                else
                    Icons.Outlined.ArrowRight,
                contentDescription = "tag"
            )
        }
    }

    val dateBtn: @Composable() () -> Unit = {

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = "date"
            )
            Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            Text(text = context.resources.getString(R.string.search_period),
                style = MaterialTheme.typography.titleSmall)
            Icon(
                modifier = Modifier.scale(1f),
                imageVector = if (isDateBox.value)
                    Icons.Outlined.ArrowDropDown
                else
                    Icons.Outlined.ArrowRight,
                contentDescription = "date "
            )
        }
    }

    val dateRangePickerHeadline: @Composable() () -> Unit = {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = context.resources.getString(R.string.search_dateRangePicker_headline),
            style = MaterialTheme.typography.bodyMedium
        )
    }

    val dateRangePickerTitle: @Composable() () -> Unit = {
        Text(
            text = "",
            style = MaterialTheme.typography.bodySmall
        )
    }

    val secretOption = RadioGroupOption(
        title = context.resources.getString(R.string.search_radioBtGroup_secret),
        options = listOf(
            context.resources.getString(R.string.search_radioBt_select),
            context.resources.getString(R.string.search_radioBt_none),
            context.resources.getString(R.string.search_radioBt_all)
        ),
        contents = {
            Row(modifier = Modifier) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = context.resources.getString(R.string.search_radioBtGroup_secret)
                )
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(
                    modifier = Modifier,
                    text = context.resources.getString(R.string.search_radioBtGroup_secret),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    )

    val secretRadioGroupState = rememberSaveable {
        mutableIntStateOf(secretOption.options.lastIndex )
    }

    val markerOption = RadioGroupOption(
        title = context.resources.getString(R.string.search_radioBtGroup_marker),
        options = listOf(
            context.resources.getString(R.string.search_radioBt_select),
            context.resources.getString(R.string.search_radioBt_none),
            context.resources.getString(R.string.search_radioBt_all)
        ),
        contents = {
            Row(modifier = Modifier) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = context.resources.getString(R.string.search_radioBtGroup_marker)
                )
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                Text(
                    modifier = Modifier,
                    text = context.resources.getString(R.string.search_radioBtGroup_marker),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    )

    val markerRadioGroupState = rememberSaveable{
        mutableIntStateOf(markerOption.options.lastIndex )
    }

    val selectedTagArray:MutableState<ArrayList<Int>> =
        rememberSaveable{ mutableStateOf(arrayListOf())  }

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .clip(shape = ShapeDefaults.ExtraSmall)
        // Critical Error
      //      .verticalScroll(rememberScrollState())
        ,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isVisibleSearchBar.value) 0.dp else 8.dp),
            query = query_title,
            onQueryChange = {
                query_title = it
            },
            onSearch = onSearch,
            active = isVisibleSearchBar.value,
            onActiveChange = {
                isVisibleSearchBar.value = it
            },
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            tonalElevation = 2.dp,
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                dividerColor =MaterialTheme.colorScheme.tertiary
            )
        ){

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val size = historyItems.size

                for ( i in 1.. size){
                    val index = size - i
                    val historyItem = historyItems[index]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp) ,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {

                        Icon(
                            modifier = Modifier
                                .clickable {  historyItems.removeAt(index) },
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )

                        Text(text = historyItem)

                        Icon(
                            modifier = Modifier
                                .clickable {  query_title = historyItem },
                            imageVector = Icons.Default.NorthWest,
                            contentDescription = null
                        )

                    }
                }


                if(historyItems.isNotEmpty()){
                    Text(
                        text = "clear all history",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clickable { historyItems.clear() },
                        textAlign = TextAlign.Center
                    )
                }



            }


        }

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(6.dp) )

        RadioButtonGroupCompose(
            state = secretRadioGroupState,
            data = secretOption.options,
            content = secretOption.contents
        )

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(6.dp) )

        RadioButtonGroupCompose(
            state = markerRadioGroupState,
            data = markerOption.options,
            content = markerOption.contents
        )

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(6.dp) )

        androidx.compose.material.IconButton(
            onClick = {
         //   hapticProcessing()
                isTagBox.value = !isTagBox.value
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            content = hashTagBtn
        )

        AssistChipGroupCompose(
            isVisible = isTagBox.value,
            setState = selectedTagArray,
        )

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(6.dp) )

        androidx.compose.material.IconButton(
            onClick = {
                //   hapticProcessing()
                isDateBox.value = !isDateBox.value
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            content = dateBtn
        )

        AnimatedVisibility(visible = isDateBox.value) {

            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(420.dp),
                title = dateRangePickerTitle,
                headline = dateRangePickerHeadline
            )
        }


    }


}

@Preview
@Composable
fun PrevSearchCompose(){
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
                SearchCompose()
            }
        }
    }
}