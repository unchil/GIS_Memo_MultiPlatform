package com.jetbrains.handson.kmm.shared

import android.content.Context
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"

    override var context: Context? = null

    override var repository: GisMemoRepository? = null
    override fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }


    override fun getRepository(context: Context?): GisMemoRepository? {
         if(repository != null){
            repository
        }else {
             context?.let {
                 repository = GisMemoRepository( DatabaseDriverFactory(it) )
             }
        }
        return repository
    }


}


actual fun getPlatform(): Platform = AndroidPlatform()