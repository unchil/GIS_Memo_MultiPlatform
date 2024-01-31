package com.jetbrains.handson.kmm.shared

import app.cash.paging.DifferCallback
class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}