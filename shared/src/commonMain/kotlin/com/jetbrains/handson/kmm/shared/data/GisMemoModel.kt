package com.jetbrains.handson.kmm.shared.data

import androidx.compose.ui.graphics.vector.ImageVector


enum class WriteMemoDataType {
    PHOTO,AUDIOTEXT,VIDEO,SNAPSHOT
}

val WriteMemoDataTypeList = listOf(
    WriteMemoDataType.SNAPSHOT,
    WriteMemoDataType.AUDIOTEXT,
    WriteMemoDataType.PHOTO,
    WriteMemoDataType.VIDEO
)
