package com.unchil.gismemo_multiplatform

import coil3.PlatformContext
import com.jetbrains.handson.kmm.shared.GisMemoRepository

object PlatformObject {
    val platform: Platform = getPlatform()
    fun getRepository(context: PlatformContext): GisMemoRepository {
        return platform.getRepository(context)!!
    }

}


/*
class PlatformHandler(context: PlatformContext) {

    private val platform: Platform = getPlatform()

    val repository: GisMemoRepository

    init {
        instance = this
        platform.context = context
        repository = platform.getRepository(context)!!
    }

    companion object {

        var instance:PlatformHandler? = null
        fun Builder(context: PlatformContext):PlatformHandler{
            if(instance == null){
                instance = PlatformHandler(context)
            }
            return instance as PlatformHandler
        }
    }

}
*/
