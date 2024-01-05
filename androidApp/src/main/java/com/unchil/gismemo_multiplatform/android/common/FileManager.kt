package com.unchil.gismemo_multiplatform.android.common

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class FileManager {


    companion object {

        const val FILE_TIMESTAMP_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        fun getFilePath(context: Context, outputfile: OUTPUTFILE): String {
            val (dir, ext) = outputfile.type()
            val timestamp = SimpleDateFormat(
                FILE_TIMESTAMP_FORMAT,
                Locale.getDefault()
            ).format(System.currentTimeMillis())

            val directory = File(context.filesDir, dir)

            val resultDir = if (directory.exists() || directory.mkdirs()) {
                directory
            } else null

            return File(resultDir, "$timestamp.$ext").canonicalPath
        }


        enum class OUTPUTFILE {
            VIDEO, IMAGE, AUDIO
        }

        fun OUTPUTFILE.type():Pair<String,String> {
            return   when(this.name){
                OUTPUTFILE.VIDEO.name ->  {
                    Pair("videos" , "mp4")
                }
                OUTPUTFILE.IMAGE.name -> {
                    Pair ("photos", "jpeg")
                }
                OUTPUTFILE.AUDIO.name -> {
                    Pair ("audios", "m4a")
                }
                else -> {
                    Pair("etc", "dat")
                }
            }
        }

    }

}