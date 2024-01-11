package com.unchil.gismemo_multiplatform

import coil3.PlatformContext
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override var context: PlatformContext? = null
    override val repository: GisMemoRepository
        get() = GisMemoRepository( DatabaseDriverFactory() )


}

actual fun getPlatform(): Platform = IOSPlatform()