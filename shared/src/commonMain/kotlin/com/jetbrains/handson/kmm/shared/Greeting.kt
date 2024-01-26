package com.jetbrains.handson.kmm.shared

import com.unchil.gismemo_multiplatform.Platform
import com.unchil.gismemo_multiplatform.getPlatform

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}