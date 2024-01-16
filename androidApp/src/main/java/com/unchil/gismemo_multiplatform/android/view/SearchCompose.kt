package com.unchil.gismemo_multiplatform.android.view

import android.app.Activity
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthWest
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCompose(
    onMessage:(() -> Unit)? = null
){

    val focusmanager = LocalFocusManager.current

    var query_title by rememberSaveable {
        mutableStateOf("")
    }

    val historyItems = remember {
        mutableStateListOf<String>()
    }
    val scrollState = rememberScrollState()

    val isVisibleSearchBar: MutableState<Boolean> = remember { mutableStateOf(false) }

    val recognizerIntent = remember { recognizerIntent }

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

    Column(
        modifier = Modifier.fillMaxSize(1f)
            .clip(shape = ShapeDefaults.ExtraSmall),
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

    }


}