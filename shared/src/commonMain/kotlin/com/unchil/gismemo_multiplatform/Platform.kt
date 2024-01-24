package com.unchil.gismemo_multiplatform

import coil3.PlatformContext
import com.jetbrains.handson.kmm.shared.GisMemoRepository

interface Platform {
    val name: String
    var context:PlatformContext?
    val repository: GisMemoRepository?

    fun getRepository(context: PlatformContext):GisMemoRepository?

    fun getCurrentTime():Long

}

expect fun getPlatform(): Platform

