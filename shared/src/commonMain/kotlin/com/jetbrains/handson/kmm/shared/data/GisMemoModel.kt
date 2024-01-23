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


object SearchQueryData {
    enum class Type {
        TITLE, SECRET, MARKER, TAG, DATE
    }
    val Types = listOf(
        Type.TITLE,
        Type.SECRET,
        Type.MARKER,
        Type.TAG,
        Type.DATE
    )

    val value = mutableMapOf<Type, Any>()

    fun clear(){
        value.clear()
    }
}

