package com.jetbrains.handson.kmm.shared.cache

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.paging3.QueryPagingSource
import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
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


internal class GisMemoDao(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = GisMemoDatabase(databaseDriverFactory.createDriver())
     val dbQuery = database.gisMemoDatabaseQueries



    internal val currentWeatherFlow:Flow<CURRENTWEATHER_TBL?> =
        dbQuery.select_CURRENTWEATHER_TBL(::mapCurrentWeatherSelecting)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)

        private fun mapCurrentWeatherSelecting(
        dt: Long,
        base: String,
        visibility: String,
        timezone: String,
        name: String,
        latitude: String,
        longitude: String,
        main: String,
        description: String,
        icon: String,
        temp: String,
        feels_like: String,
        pressure: String,
        humidity: String,
        temp_min: String,
        temp_max: String,
        speed: String,
        deg: String,
        aa: String,
        type: String,
        country: String,
        sunrise: String,
        sunset: String
    ): CURRENTWEATHER_TBL
    {
        return  CURRENTWEATHER_TBL(
            dt = dt ,
            base = base,
            visibility =  visibility.toInt(),
            timezone = timezone.toLong(),
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
            sunrise = sunrise.toLong(),
            sunset =  sunset.toLong()
        )
    }


    internal fun insertCurrentWeather(it: CURRENTWEATHER_TBL) {
        dbQuery.transaction {

            dbQuery.trancate_CURRENTWEATHER_TBL()

            dbQuery.insert_CURRENTWEATHER_TBL(
                dt  = it.dt,
                base   = it.base,
                visibility  = it.visibility.toString() ,
                timezone = it.timezone.toString(),
                name  = it.name,
                latitude =  it.latitude.toString(),
                longitude = it.longitude.toString(),
                main = it.main,
                description = it.description,
                icon = it.icon,
                temp = it.temp.toString(),
                feels_like = it.feels_like.toString(),
                pressure = it.pressure.toString(),
                humidity = it.humidity.toString(),
                temp_min = it.temp_min.toString(),
                temp_max = it.temp_max.toString(),
                speed  = it.speed.toString(),
                deg = it.deg.toString(),
                aa  = it.all.toString(),
                type = it.type.toString(),
                country  = it.country,
                sunrise = it.sunrise.toString(),
                sunset = it.sunset.toString()
            )
        }
    }

    internal fun insertCurrentLocation(it: LatLngAlt) {
        dbQuery.transaction {
            dbQuery.insert_CURRENTLOCATION_TBL(
                dt = it.collectTime,
                latitude = it.latitude.toString(),
                longitude =  it.longitude.toString(),
                altitude = it.altitude.toString()
            )
        }
    }

    internal fun insertMemo(it: MEMO_TBL){
        dbQuery.transaction {


            dbQuery.insert_MEMO_TBL(
                id = it.id,
                latitude = it.latitude.toString(),
                longitude = it.longitude.toString(),
                altitude = it.altitude.toString(),
                isSecret = if (it.isSecret) 1 else 0,
                isPin = if(it.isPin) 1 else 0 ,
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
            dbQuery.update_MEMO_TBL_Marker(isPin = if(isMarker) 1 else 0 , id = id)
        }
    }

    internal fun updateMemoSecret(id:Long, isSecret: Boolean){
        dbQuery.transaction {
            dbQuery.update_MEMO_TBL_Secret(isSecret = if(isSecret) 1 else 0, id =  id)
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
        visibility: String,
        timezone: String,
        name: String,
        latitude: String,
        longitude: String,
        main: String,
        description: String,
        icon: String,
        temp: String,
        feels_like: String,
        pressure: String,
        humidity: String,
        temp_min: String,
        temp_max: String,
        speed: String,
        deg: String,
        aa: String,
        type: String,
        country: String,
        sunrise: String,
        sunset: String
    ): MEMO_WEATHER_TBL
    {
        return  MEMO_WEATHER_TBL(
            id = id ,
            base = base,
            visibility =  visibility.toInt(),
            timezone = timezone.toLong(),
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
            sunrise = sunrise.toLong(),
            sunset =  sunset.toLong()
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
                visibility = it.visibility.toString(),
                timezone = it.timezone.toString(),
                name  = it.name,
                latitude  = it.latitude.toString(),
                longitude = it.longitude.toString(),
                main  = it.main,
                description = it.description,
                icon = it.icon,
                temp = it.temp.toString(),
                feels_like = it.feels_like.toString(),
                pressure = it.pressure.toString(),
                humidity = it.humidity.toString(),
                temp_min = it.temp_min.toString(),
                temp_max = it.temp_max.toString(),
                speed = it.speed.toString(),
                deg = it.deg.toString(),
                aa = it.all.toString(),
                type = it.type.toString(),
                country = it.country,
                sunrise = it.sunrise.toString(),
                sunset = it.sunset.toString()
            )
        }
    }


    internal val memoListFlow : Flow<List<MEMO_TBL>> =
        dbQuery.select_MEMO_TBL_All(::mapMemoSelecting)
            .asFlow()
            .mapToList(Dispatchers.IO)

    internal fun selectMemo(it:Long):MEMO_TBL? {
        return dbQuery.select_MEMO_TBL_ID(it, ::mapMemoSelecting).executeAsOneOrNull()
    }

    internal val markerListFlow:  Flow<List<MEMO_TBL>> =
        dbQuery.select_MEMO_TBL_Marker(::mapMemoSelecting)
            .asFlow()
            .mapToList(Dispatchers.IO)


    private fun mapMemoSelecting(
        id : Long,
        latitude  : String,
        longitude : String,
        altitude : String,
        isSecret : Long,
        isPin  : Long,
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
            isSecret = isSecret.toInt() == 1,
            isPin = isPin.toInt() == 1,
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


    internal fun memoFileListFlow(id:Long): Flow<List<MEMO_FILE_TBL>> =
        dbQuery.select_MEMO_FILE_TBL_ID( id,
            mapper = {  id, type, indexR, subIndex, filePath ->
                MEMO_FILE_TBL(
                    id = id,
                    type = type,
                    index = indexR.toInt(),
                    subIndex = subIndex.toInt(),
                    filePath = filePath
                )
            }).asFlow()
            .mapToList(Dispatchers.IO)

    internal fun selectMemoFile(id:Long):List<MEMO_FILE_TBL>{
        return dbQuery.select_MEMO_FILE_TBL_ID( id,
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

    internal fun memoTextListFlow(id:Long):Flow<List<MEMO_TEXT_TBL>> =
        dbQuery.select_MEMO_TEXT_TBL(id,
            mapper = { id, indexR, comment ->
                MEMO_TEXT_TBL(
                    id = id,
                    index =  indexR.toInt(),
                    comment = comment
                )
            }).asFlow()
            .mapToList(Dispatchers.IO)

    internal fun selectMemoText(id:Long):List<MEMO_TEXT_TBL>{
        return dbQuery.select_MEMO_TEXT_TBL(id,
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


    internal val keyedQueryPagingSource : PagingSource<Long, MEMO_TBL> =
         QueryPagingSource (
            transacter = dbQuery,
            context = Dispatchers.IO,
            pageBoundariesProvider = { anchor ,  limit ->
                dbQuery.pageBoundaries_MEMO_TBL( limit = limit, anchor = anchor?: 0)
            },
            queryProvider = { beginInclusive:Long, endExclusive ->
                dbQuery.keyedQuery_MEMO_TBL(
                    beginInclusive = beginInclusive,
                    endExclusive = endExclusive,
                    ::mapMemoSelecting
                )
            }
        )



    internal val  offsetQueryPagingSource: PagingSource<Int, MEMO_TBL>  =
         QueryPagingSource(
            countQuery = dbQuery.countMEMO_TBL(),
            transacter = dbQuery,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                dbQuery.pagingMEMO_TBL(limit, offset, ::mapMemoSelecting)
            }
         )








}
