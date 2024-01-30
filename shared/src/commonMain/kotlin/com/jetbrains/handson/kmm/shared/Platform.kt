package com.jetbrains.handson.kmm.shared

import coil3.PlatformContext

interface Platform {
    val name: String
    var context:PlatformContext?
    val repository: GisMemoRepository?

    fun getRepository(context: PlatformContext? ):GisMemoRepository?

    fun getCurrentTime():Long

}

expect fun getPlatform(): Platform

