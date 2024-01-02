package com.unchil.gismemo_multiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform