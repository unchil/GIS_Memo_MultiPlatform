package com.unchil.gismemo_multiplatform.android

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.fetch.NetworkFetcher
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.unchil.gismemo_multiplatform.Platform
import com.unchil.gismemo_multiplatform.getPlatform

class GisMemoApp: Application(), SingletonImageLoader.Factory {


    val platform: Platform

    init {
        instance = this
        platform = getPlatform()
    }

    override fun onCreate() {
        super.onCreate()
        platform.context = applicationContext
        platform.repository?.let {
            repository = it
        }

    }

    companion object {
        var instance:GisMemoApp? = null
        var repository:GisMemoRepository? = null
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components{
                add(NetworkFetcher.Factory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()

            }.apply {
                logger(DebugLogger())
            }.build()
    }

}