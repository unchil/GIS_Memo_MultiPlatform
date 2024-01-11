package com.jetbrains.handson.kmm.shared.cache


import  com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_FILE_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TAG_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TEXT_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_WEATHER_TBL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import app.cash.sqldelight.paging3.QueryPagingSource
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt


internal class GisMemoDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = GisMemoDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.gisMemoDatabaseQueries


    val selectCurrentWeatherFlow:Flow<CURRENTWEATHER_TBL?> = flow {
        emit(
            selectCurrentWeather()
        )
    }.flowOn(Dispatchers.IO)


    internal fun selectCurrentWeather(): CURRENTWEATHER_TBL? {
        return dbQuery.select_CURRENTWEATHER_TBL(::mapCurrentWeatherSelecting).executeAsOneOrNull()
    }

    private fun mapCurrentWeatherSelecting(
        dt: Long,
        base: String,
        visibility: Long,
        timezone: Long,
        name: String,
        latitude: Long,
        longitude: Long,
        main: String,
        description: String,
        icon: String,
        temp: Long,
        feels_like: Long,
        pressure: Long,
        humidity: Long,
        temp_min: Long,
        temp_max: Long,
        speed: Long,
        deg: Long,
        aa: Long,
        type: Long,
        country: String,
        sunrise: Long,
        sunset: Long
    ): CURRENTWEATHER_TBL
    {
        return  CURRENTWEATHER_TBL(
            dt = dt ,
            base = base,
            visibility =  visibility.toInt(),
            timezone = timezone,
            name = name,
            latitude = latitude.toFloat(),
            longitude = longitude.toFloat(),
            main = main ,
            description = description ,
            icon = icon,
            temp = temp.toFloat(),
            feels_like = feels_like.toFloat(),
            pressure =  pressure.toFloat(),
            humidity =  humidity.toFloat(),
            temp_min = temp_min.toFloat(),
            temp_max =  temp_max.toFloat(),
            speed =  speed.toFloat(),
            deg =  deg.toFloat(),
            all = aa.toInt(),
            type =  type.toInt(),
            country = country,
            sunrise = sunrise,
            sunset =  sunset
        )
    }


    internal fun insertCurrentWeather(it: CURRENTWEATHER_TBL) {
        dbQuery.transaction {

            dbQuery.trancate_CURRENTWEATHER_TBL()

            dbQuery.insert_CURRENTWEATHER_TBL(
                dt  = it.dt,
                base   = it.base,
                visibility  = it.visibility.toLong() ,
                timezone = it.timezone.toLong(),
                name  = it.name,
                latitude =  it.latitude.toLong(),
                longitude = it.longitude.toLong(),
                main = it.main,
                description = it.description,
                icon = it.icon,
                temp = it.temp.toLong(),
                feels_like = it.feels_like.toLong(),
                pressure = it.pressure.toLong(),
                humidity = it.humidity.toLong(),
                temp_min = it.temp_min.toLong(),
                temp_max = it.temp_max.toLong(),
                speed  = it.speed.toLong(),
                deg = it.deg.toLong(),
                aa  = it.all.toLong(),
                type = it.type.toLong(),
                country  = it.country,
                sunrise = it.sunrise,
                sunset = it.sunset
            )
        }
    }

    internal fun insertCurrentLocation(it: LatLngAlt) {
        dbQuery.transaction {
            dbQuery.insert_CURRENTLOCATION_TBL(
                dt = it.collectTime,
                latitude = it.latitude.toLong(),
                longitude =  it.longitude.toLong(),
                altitude = it.altitude.toLong()
            )
        }
    }

    internal fun insertMemo(it: MEMO_TBL){
        dbQuery.transaction {


            dbQuery.insert_MEMO_TBL(
                id = it.id,
                latitude = it.latitude.toLong(),
                longitude = it.longitude.toLong(),
                altitude = it.altitude.toLong(),
                isSecret = it.isSecret,
                isPin = it.isPin,
                title = it.title,
                snippets = it.snippets,
                desc = it.desc,
                snapshot = it.snapshot,
                snapshotCnt = it.snapshotCnt.toLong(),
                textCnt= it.textCnt.toLong(),
                photoCnt = it.photoCnt.toLong(),
                videoCnt = it.videoCnt.toLong()
            )
        }
    }

    internal fun updateMemoMarker(id:Long, isMarker:Boolean){
        dbQuery.transaction {
            dbQuery.update_MEMO_TBL_Marker(isPin = isMarker, id = id)
        }
    }

    internal fun updateMemoSecret(id:Long, isSecret: Boolean){
        dbQuery.transaction {
            dbQuery.update_MEMO_TBL_Secret(isSecret = isSecret, id =  id)
        }
    }

    internal fun insertMemoTag(tagList:List<MEMO_TAG_TBL>)   {
    dbQuery.transaction {
        tagList.forEach {
            dbQuery.insert_MEMO_TAG_TBL(
                id = it.id,
                indexR = it.index.toLong()
            )
        }
    }
    }

    internal fun insertMemoFile(fileList:List<MEMO_FILE_TBL>) {
    dbQuery.transaction {
        fileList.forEach {
            dbQuery.insert_MEMO_FILE_TBL(
                id = it.id,
                type = it.type,
                indexR = it.index.toLong(),
                subIndex = it.subIndex.toLong(),
                filePath = it.filePath
            )
        }
    }
    }

    internal fun insertMemoText(commentList:List<MEMO_TEXT_TBL>) {
        dbQuery.transaction {
            commentList.forEach {
                dbQuery.insert_MEMO_TEXT_TBL(
                    id = it.id,
                    indexR = it.index.toLong(),
                    comment = it.comment
                )
            }
        }
    }

    internal fun updateMemoTag(id:Long, snippets: String, tagList:List<MEMO_TAG_TBL>){
        dbQuery.transaction {
            dbQuery.update_MEMO_TBL_Snippets(snippets = snippets, id = id)
            dbQuery.delete_MEMO_TAG_TBL_ID(id = id)
            tagList.forEach {
                dbQuery.insert_MEMO_TAG_TBL(
                    id = it.id,
                    indexR = it.index.toLong()
                )
            }
        }
    }

    internal fun deleteMemo(it:Long) {
        dbQuery.transaction {
            dbQuery.delete_MEMO_TBL_ID(it)
            dbQuery.delete_MEMO_FILE_TBL_ID(it)
            dbQuery.delete_MEMO_TEXT_TBL_ID(it)
            dbQuery.delete_MEMO_TAG_TBL_ID(it)
            dbQuery.delete_MEMO_WEATHER_TBL_ID(it)
        }
    }

    internal fun deleteAllMemo(){
        dbQuery.transaction {
            dbQuery.trancate_MEMO_TBL()
            dbQuery.trancate_MEMO_FILE_TBL()
            dbQuery.trancate_MEMO_TEXT_TBL()
            dbQuery.trancate_MEMO_TAG_TBL()
            dbQuery.trancate_MEMO_WEATHER_TBL()
        }
    }

    private fun mapMemoWeatherSelecting(
        id: Long,
        base: String,
        visibility: Long,
        timezone: Long,
        name: String,
        latitude: Long,
        longitude: Long,
        main: String,
        description: String,
        icon: String,
        temp: Long,
        feels_like: Long,
        pressure: Long,
        humidity: Long,
        temp_min: Long,
        temp_max: Long,
        speed: Long,
        deg: Long,
        aa: Long,
        type: Long,
        country: String,
        sunrise: Long,
        sunset: Long
    ): MEMO_WEATHER_TBL
    {
        return  MEMO_WEATHER_TBL(
            id = id ,
            base = base,
            visibility =  visibility.toInt(),
            timezone = timezone,
            name = name,
            latitude = latitude.toFloat(),
            longitude = longitude.toFloat(),
            main = main ,
            description = description ,
            icon = icon,
            temp = temp.toFloat(),
            feels_like = feels_like.toFloat(),
            pressure =  pressure.toFloat(),
            humidity =  humidity.toFloat(),
            temp_min = temp_min.toFloat(),
            temp_max =  temp_max.toFloat(),
            speed =  speed.toFloat(),
            deg =  deg.toFloat(),
            all = aa.toInt(),
            type =  type.toInt(),
            country = country,
            sunrise = sunrise,
            sunset =  sunset
        )
    }

    internal fun selectMemoWeather(it:Long):MEMO_WEATHER_TBL?{
       return  dbQuery.select_MEMO_WEATHER_TBL_ID(it,
            mapper = ::mapMemoWeatherSelecting).executeAsOneOrNull()
    }




    internal fun insertMemoWeather(it:CURRENTWEATHER_TBL){
        dbQuery.transaction {
            dbQuery.insert_MEMO_WEATHER_TBL(
                id = it.dt,
            base = it.base,
            visibility = it.visibility.toLong(),
            timezone = it.timezone,
            name  = it.name,
            latitude  = it.latitude.toLong(),
            longitude = it.longitude.toLong(),
            main  = it.main,
            description = it.description,
            icon = it.icon,
            temp = it.temp.toLong(),
            feels_like = it.feels_like.toLong(),
            pressure = it.pressure.toLong(),
            humidity = it.humidity.toLong(),
            temp_min = it.temp_min.toLong(),
            temp_max = it.temp_max.toLong(),
            speed = it.speed.toLong(),
            deg = it.deg.toLong(),
            aa = it.all.toLong(),
            type = it.type.toLong(),
            country = it.country,
            sunrise = it.sunrise,
            sunset = it.sunset
            )
        }
    }

    internal fun selectMemo(it:Long):MEMO_TBL? {
        return dbQuery.select_MEMO_TBL_ID(it, ::mapMemoSelecting).executeAsOneOrNull()
    }

    val selectMarkerMemoListFlow: Flow<List<MEMO_TBL>> = flow {
            emit(dbQuery.select_MEMO_TBL_Marker(::mapMemoSelecting).executeAsList()   )
    }.flowOn(Dispatchers.IO)

    private fun mapMemoSelecting(
        id : Long,
        latitude  : Long,
        longitude : Long,
        altitude : Long,
        isSecret : Boolean?,
        isPin  : Boolean?,
        title : String,
        snippets: String,
        desc : String,
        snapshot : String,
        snapshotCnt  : Long,
        textCnt   : Long,
        photoCnt   : Long,
        videoCnt   : Long,
    ): MEMO_TBL{
        return MEMO_TBL(
            id = id,
            latitude = latitude.toFloat(),
            longitude = longitude.toFloat(),
            altitude = altitude.toFloat(),
            isSecret= isSecret?: false,
            isPin = isPin?: false,
            title = title,
            snippets = snippets,
            desc = desc,
            snapshot = snapshot,
            snapshotCnt = snapshotCnt.toInt(),
            textCnt = textCnt.toInt(),
            photoCnt = photoCnt.toInt(),
            videoCnt = videoCnt.toInt()
        )
    }

     fun selectMemoFileListFlow(it:Long):Flow<List<MEMO_FILE_TBL>> = flow {
        emit(
            selectMemoFile(it)
        )
    }.flowOn(Dispatchers.IO)

internal fun selectMemoFile(it:Long):List<MEMO_FILE_TBL>{
    return dbQuery.select_MEMO_FILE_TBL_ID( it,
        mapper = {  id, type, indexR, subIndex, filePath ->
            MEMO_FILE_TBL(
                id = id,
                type = type,
                index = indexR.toInt(),
                subIndex = subIndex.toInt(),
                filePath = filePath
            )
    }).executeAsList()
}

    fun selectMemoTextListFlow(it:Long):Flow<List<MEMO_TEXT_TBL>> = flow {
        emit(
            selectMemoText(it)
        )
    }.flowOn(Dispatchers.IO)

    internal fun selectMemoText(it:Long):List<MEMO_TEXT_TBL>{
        return dbQuery.select_MEMO_TEXT_TBL(it,
            mapper = { id, indexR, comment ->
                MEMO_TEXT_TBL(
                    id = id,
                    index =  indexR.toInt(),
                    comment = comment
                )
            }).executeAsList()
    }

    internal fun selectMemoTags(it:Long):List<MEMO_TAG_TBL>{
        return dbQuery.select_MEMO_TAG_TBL_ID(it,
            mapper = { id, indexR ->
                MEMO_TAG_TBL(
                    id = id,
                    index = indexR.toInt()
                )
            }).executeAsList()
    }



    val pagingSource =
        QueryPagingSource (
            transacter = dbQuery,
            context = Dispatchers.IO,
            pageBoundariesProvider = { anchor ,  limit ->
                dbQuery.pageBoundaries_MEMO_TBL(anchor = anchor?: 0, limit = limit)
            },
            queryProvider = { beginInclusive:Long, endExclusive ->
                dbQuery.keyedQuery_MEMO_TBL(beginInclusive = beginInclusive, endExclusive = endExclusive, ::mapMemoSelecting)
            }
        )










}
