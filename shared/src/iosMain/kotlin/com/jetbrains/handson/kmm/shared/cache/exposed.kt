package com.jetbrains.handson.kmm.shared.cache


import app.cash.paging.PagingCollectionViewController
import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("unused", "UNUSED_PARAMETER") // Used to export types to Objective-C / Swift.
fun exposedTypes(
    pagingCollectionViewController: PagingCollectionViewController<*>,
    mutableSharedFlow: MutableSharedFlow<*>,
): Unit = throw AssertionError()

@Suppress("unused") // Used to export types to Objective-C / Swift.
fun <T> mutableSharedFlow(extraBufferCapacity: Int) = MutableSharedFlow<T>(extraBufferCapacity = extraBufferCapacity)
