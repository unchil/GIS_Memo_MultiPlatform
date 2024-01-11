package com.unchil.gismemo_multiplatform

import android.content.Context
import coil3.PlatformContext
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"

    override var context: Context? = null

    override var repository: GisMemoRepository? = null


    override fun getRepository(context: Context): GisMemoRepository? {
         if(repository != null){
            repository
        }else {
            repository = GisMemoRepository( DatabaseDriverFactory(context) )
        }
        return repository
    }


}


actual fun getPlatform(): Platform = AndroidPlatform()