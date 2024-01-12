package com.unchil.gismemo_multiplatform

import coil3.PlatformContext
import com.jetbrains.handson.kmm.shared.GisMemoRepository

class PlatformHandler {

    private val platform: Platform = getPlatform()

    fun setContext(context: PlatformContext){
        platform.context = context
    }

    fun getRepository(context: PlatformContext):GisMemoRepository {
        platform.context = context
        return platform.getRepository(context)!!
    }
}

