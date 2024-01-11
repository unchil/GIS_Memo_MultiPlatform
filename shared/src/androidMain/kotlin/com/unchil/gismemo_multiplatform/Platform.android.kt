package com.unchil.gismemo_multiplatform

import android.content.Context
import coil3.PlatformContext
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"

    override var context: PlatformContext? = null
        set(value) {
            field = value as PlatformContext
        }

    override val repository: GisMemoRepository?
        get() =
            if(this.context == null) {
                null
            } else {
                GisMemoRepository( DatabaseDriverFactory(this.context!!) )
            }


}


actual fun getPlatform(): Platform = AndroidPlatform()