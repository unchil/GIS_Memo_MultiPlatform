package com.jetbrains.handson.kmm.shared

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData

import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory
import com.jetbrains.handson.kmm.shared.cache.GisMemoDao
import com.jetbrains.handson.kmm.shared.entity.CURRENTLOCATION_TBL
import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.DrawingPolyline
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
import com.jetbrains.handson.kmm.shared.entity.MEMO_FILE_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TAG_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TEXT_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_WEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.WriteMemoDataType
import com.jetbrains.handson.kmm.shared.entity.WriteMemoDataTypeList
import com.jetbrains.handson.kmm.shared.network.GisMemoApi
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch



class GisMemoRepository(databaseDriverFactory: DatabaseDriverFactory) {

       private val gisMemoDao = GisMemoDao(databaseDriverFactory)
       private val api = GisMemoApi()


    val _currentWeather:MutableStateFlow<CURRENTWEATHER_TBL?>
        = MutableStateFlow(null)

    val currentAudioText: MutableStateFlow<List<Pair<String, List<Url>>>>
            = MutableStateFlow( listOf())

    val currentPhoto:  MutableStateFlow<List<Url>>
            = MutableStateFlow( listOf())

    val currentVideo: MutableStateFlow<List<Url>>
            = MutableStateFlow( listOf())

    val currentSnapShot: MutableStateFlow<List<Url>>
            = MutableStateFlow( listOf())

    val _markerMemoList:MutableStateFlow<List<MEMO_TBL>>
            = MutableStateFlow(listOf())

    val selectedMemo:MutableStateFlow<MEMO_TBL?>
            = MutableStateFlow(null)


    var selectedTagList: MutableStateFlow<ArrayList<Int>>
            = MutableStateFlow(arrayListOf())

    val selectedWeather:MutableStateFlow<MEMO_WEATHER_TBL?>
        = MutableStateFlow(null)

    val detailAudioText: MutableStateFlow<List<Pair<String, List<Url>>>>
            = MutableStateFlow( listOf())

    val detailPhoto:  MutableStateFlow<List<Url>>
            = MutableStateFlow( listOf())

    val detailVideo: MutableStateFlow<List<Url>>
            = MutableStateFlow( listOf())

    val detailSnapShot: MutableStateFlow<List<Url>>
            = MutableStateFlow( listOf())


    val currentIsDrawing: MutableStateFlow<Boolean>
            = MutableStateFlow(false)

    val currentIsEraser: MutableStateFlow<Boolean>
            = MutableStateFlow(false)


    val currentIsLock: MutableStateFlow<Boolean>
            = MutableStateFlow(false)

    val currentIsMarker: MutableStateFlow<Boolean>
            = MutableStateFlow(false)


    val currentPolylineList:MutableStateFlow<List<DrawingPolyline>>     = MutableStateFlow(listOf())

    fun clearCurrentValue(){
        selectedTagList.value = arrayListOf()

        currentIsLock.value = false
        currentIsMarker.value = false
        currentIsDrawing.value = false
        currentIsEraser.value = false
        currentPolylineList.value = listOf()
        currentSnapShot.value = listOf()
    }



    suspend fun insertMemo(
        id:Long,
        isLock:Boolean,
        isMark:Boolean,
        selectTagArrayList:ArrayList<Int>,
        title:String,
        desc:String,
        snippets: String,
        location: CURRENTLOCATION_TBL
    )  {


        val memoFileTblList = mutableListOf<MEMO_FILE_TBL>()
        val memoTextTblList = mutableListOf<MEMO_TEXT_TBL>()

        val snapshot = currentSnapShot.value.first().encodedPath

        val memoTbl = MEMO_TBL(
            id = id,
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            isSecret = isLock,
            isPin = isMark,
            title = title,
            snippets = snippets,
            snapshotCnt = currentSnapShot.value.size,
            textCnt = currentAudioText.value.size,
            photoCnt = currentPhoto.value.size,
            videoCnt = currentVideo.value.size,
            desc = desc,
            snapshot = snapshot
        )

        gisMemoDao.insertMemo(memoTbl)

        currentSnapShot.value.forEachIndexed { index, uri ->
            memoFileTblList.add(
                MEMO_FILE_TBL(
                    id = id,
                    type = WriteMemoDataType.SNAPSHOT.name,
                    index = index,
                    subIndex = 0,
                    filePath = uri.encodedPath
                )
            )
        }

        currentPhoto.value.forEachIndexed { index, uri ->
            memoFileTblList.add(
                MEMO_FILE_TBL(
                    id = id,
                    type = WriteMemoDataType.PHOTO.name,
                    index = index,
                    subIndex = 0,
                    filePath = uri.encodedPath
                )
            )
        }

        currentVideo.value.forEachIndexed { index, uri ->
            memoFileTblList.add(
                MEMO_FILE_TBL(
                    id = id,
                    type = WriteMemoDataType.VIDEO.name,
                    index = index,
                    subIndex = 0,
                    filePath = uri.encodedPath
                )
            )
        }


        currentAudioText.value.forEachIndexed { index, pairData ->
            memoTextTblList.add(
                MEMO_TEXT_TBL(
                    id = id,
                    index = index,
                    comment = pairData.first
                )
            )

            pairData.second.forEachIndexed { subIndex, uri ->
                memoFileTblList.add(
                    MEMO_FILE_TBL(
                        id = id,
                        type = WriteMemoDataType.AUDIOTEXT.name,
                        index = index,
                        subIndex = subIndex,
                        filePath = uri.encodedPath
                    )
                )
            }
        }

        val memoTagTblList = mutableListOf(MEMO_TAG_TBL(id = id, index = 10000))
        selectTagArrayList.forEach {
            memoTagTblList.add(MEMO_TAG_TBL(id = id, index = it))
        }

        gisMemoDao.insertMemoTag(memoTagTblList)
        gisMemoDao.insertMemoFile(memoFileTblList)
        gisMemoDao.insertMemoText(memoTextTblList)
        _currentWeather.value?.let {
            gisMemoDao.insertMemoWeather(it)
        }

        initMemoItem()


    }

    suspend fun initMemoItem(){

        WriteMemoDataTypeList.forEach {

            when(it){
                WriteMemoDataType.PHOTO -> {
                    val newMemoItem = currentPhoto.value.toMutableList()
                    newMemoItem.clear()
                    currentPhoto.emit(newMemoItem)
                }

                WriteMemoDataType.AUDIOTEXT -> {
                    val newMemoItem = currentAudioText.value.toMutableList()
                    newMemoItem.clear()
                    currentAudioText.emit(newMemoItem)
                }
                WriteMemoDataType.VIDEO -> {
                    val newMemoItem = currentVideo.value.toMutableList()
                    newMemoItem.clear()
                    currentVideo.emit(newMemoItem)
                }
                WriteMemoDataType.SNAPSHOT -> {
                    val newMemoItem = currentSnapShot.value.toMutableList()
                    newMemoItem.clear()
                    currentSnapShot.emit(newMemoItem)
                }

            }
        }

    }

    fun deleteMemoItem( type:WriteMemoDataType,  index:Int) {
        when(type){
            WriteMemoDataType.PHOTO -> {
                val newMemoItem = currentPhoto.value.toMutableList()
                newMemoItem.removeAt(index)
               // currentPhoto.emit(newMemoItem)
                currentPhoto.value = newMemoItem
            }
            WriteMemoDataType.AUDIOTEXT -> {
                val newMemoItem = currentAudioText.value.toMutableList()
                newMemoItem.removeAt(index)
               // currentAudioText.emit(newMemoItem)
                currentAudioText.value = newMemoItem
            }
            WriteMemoDataType.VIDEO -> {
                val newMemoItem = currentVideo.value.toMutableList()
                newMemoItem.removeAt(index)
                //currentVideo.emit(newMemoItem)
                currentVideo.value = newMemoItem
            }
            WriteMemoDataType.SNAPSHOT -> {
                val newMemoItem = currentSnapShot.value.toMutableList()
                newMemoItem.removeAt(index)
                //currentSnapShot.emit(newMemoItem)
                currentSnapShot.value = newMemoItem
            }
        }
    }

    fun deleteMemo(id:Long){
        gisMemoDao.deleteMemo(id)
    }

    fun deleteAllMemo(){
        gisMemoDao.deleteAllMemo()
    }

    suspend fun setMarkerMemoList() {
        gisMemoDao.selectMarkerMemoListFlow.collectLatest {
            _markerMemoList.emit(it)
        }
    }

    fun getShareMemoData(   id:Long, completeHandle:(attachments:ArrayList<Url>,  comments:ArrayList<String>)->Unit )
            = CoroutineScope(Dispatchers.IO).launch {
        val attachments = arrayListOf<Url>()
        val comments = arrayListOf<String>()


        gisMemoDao.selectMemoFile(id).forEach {
            attachments.add(Url(it.filePath))
        }

        gisMemoDao.selectMemoText(id).forEach {
            comments.add(it.comment)
        }

        completeHandle(attachments, comments)
    }

    fun setMemo(id:Long){
        CoroutineScope(Dispatchers.IO).launch {
            selectedMemo.value = gisMemoDao.selectMemo(id)
        }
    }

    fun setTags(id:Long){
        CoroutineScope(Dispatchers.IO).launch {
            val tagArrayList = arrayListOf<Int>()
            gisMemoDao.selectMemoTags(id).forEach {
                tagArrayList.add(it.index)
            }
            selectedTagList.value = tagArrayList
        }
    }

     fun setWeather(id:Long){
        selectedWeather.value = gisMemoDao.selectMemoWeather(id)
    }

    suspend fun setFiles(id:Long){

        gisMemoDao.selectMemoFileListFlow(id).collectLatest {
            val currentSnapShotList = it.filter {
                it.type ==  WriteMemoDataType.SNAPSHOT.name
            }.sortedBy {
                it.index
            }.map {
                Url(it.filePath)
            }

            detailSnapShot.emit( currentSnapShotList )

            val currentPhotoList = it.filter {
                it.type ==  WriteMemoDataType.PHOTO.name
            }.sortedBy {
                it.index
            }.map {
                Url(it.filePath)
            }

            detailPhoto.emit(  currentPhotoList )

            val currentVideoList = it.filter {
                it.type == WriteMemoDataType.VIDEO.name
            }.sortedBy {
                it.index
            }.map {
                Url(it.filePath)
            }

            detailVideo.emit(  currentVideoList  )

            gisMemoDao.selectMemoTextListFlow(id).collectLatest {memoTextTblList ->

                val audiTextList = mutableListOf<Pair<String,List<Url>>>()
                val audioTextFileList = it.filter { it.type ==  WriteMemoDataType.AUDIOTEXT.name}

                memoTextTblList.forEach {commentList ->
                    audiTextList.add(
                        Pair(
                            commentList.comment,
                            audioTextFileList.filter {
                                it.index == commentList.index
                            }.sortedBy {
                                it.subIndex
                            }.map {
                                Url(it.filePath)
                            }
                        )
                    )
                }
                detailAudioText.value = audiTextList
            }

        }

    }


    fun updateTagList(id:Long, selectTagList: ArrayList< Int>, snippets:String){

        val memoTagTblList = mutableListOf(MEMO_TAG_TBL(id = id, index = 10000))
        selectTagList.forEach {
            memoTagTblList.add(MEMO_TAG_TBL(id = id, index = it))
        }

        gisMemoDao.updateMemoTag(id, snippets, memoTagTblList)
    }

    fun updateMark(id:Long, isMark:Boolean){
        gisMemoDao.updateMemoMarker(id, isMark)
    }

    fun updateSecret(id:Long, isSecret:Boolean){
        gisMemoDao.updateMemoSecret(id, isSecret)
    }

    fun insertCurrentLocation(it:LatLngAlt){
        gisMemoDao.insertCurrentLocation(it)
    }

    @Throws(Exception::class)
    suspend fun getWeatherData(
            lat:String,
            lon:String,
            units:String,
            appid:String
    ): CURRENTWEATHER_TBL {
        val currentWeather = gisMemoDao.selectCurrentWeather()
        return if (currentWeather != null ) {
            currentWeather
        } else {
            api.getWeatherData(
                lat = lat,
                lon = lon,
                units = units,
                appid = appid
            ).also {
                gisMemoDao.insertCurrentWeather(it)
            }
        }
    }



    val getMemoListPagingFlow: Flow<PagingData<MEMO_TBL>> = flow  {
          Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders =  false
            ),
            pagingSourceFactory = {
                gisMemoDao.pagingSource
            }
        )
    }






}
