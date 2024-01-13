package com.jetbrains.handson.kmm.shared.data

import androidx.compose.ui.graphics.vector.ImageVector


object WriteMemoData {
    enum class Type {
        PHOTO,AUDIOTEXT,VIDEO,SNAPSHOT
    }


    val Types = listOf(
        Type.SNAPSHOT,
        Type.AUDIOTEXT,
        Type.PHOTO,
        Type.VIDEO
    )
}
