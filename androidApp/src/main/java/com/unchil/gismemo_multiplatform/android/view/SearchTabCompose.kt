package com.unchil.gismemo_multiplatform.android.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.unchil.gismemo_multiplatform.android.model.TagInfoDataObject


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AssistChipGroupView(
    modifier: Modifier = Modifier,
    isVisible:Boolean =true,
    @SuppressLint("MutableCollectionMutableState") setState: MutableState<ArrayList<Int>> = mutableStateOf( arrayListOf()),
    content: @Composable (( ) -> Unit)? = null
){

    val context = LocalContext.current

    TagInfoDataObject.clear()
    setState.value.forEach {
        TagInfoDataObject.entries[it].isSet.value = true
    }

    val  lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val itemModifier = Modifier.wrapContentSize()
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

    AnimatedVisibility(visible = isVisible) {
        Column (
            modifier = Modifier.then(modifier)
        ){
            LazyHorizontalStaggeredGrid(
                rows =  StaggeredGridCells.Fixed(4),
                modifier  = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                state = lazyStaggeredGridState,
                contentPadding =  PaddingValues(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalItemSpacing = 6.dp,
                userScrollEnabled = true,
            ){
                itemsIndexed(TagInfoDataObject.entries) { index, it ->
                    AssistChip(
                        modifier = itemModifier,
                        shape = ShapeDefaults.ExtraSmall,
                        onClick = {
                          //  hapticProcessing()
                            it.isSet.value = !it.isSet.value
                            if (it.isSet.value)  setState.value.add(index) else   setState.value.remove(index)
                        },
                        label = {
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    imageVector = it.icon,
                                    contentDescription = "",
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                                Text(
                                    text = context.resources.getString(it.name),
                                    style = MaterialTheme.typography.labelMedium)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector =   if (it.isSet.value) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                                contentDescription = "",
                                modifier = Modifier.size(AssistChipDefaults.IconSize),

                                )
                        },
                    )
                } // itemsIndexed
            }
            content?.let {
                it()
            }
        }
    }
}

