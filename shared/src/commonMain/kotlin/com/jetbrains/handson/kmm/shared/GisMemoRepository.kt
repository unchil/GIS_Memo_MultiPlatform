package com.jetbrains.handson.kmm.shared


import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.jetbrains.handson.kmm.shared.cache.DatabaseDriverFactory
import com.jetbrains.handson.kmm.shared.cache.GisMemoDao
import com.jetbrains.handson.kmm.shared.data.WriteMemoDataType
import com.jetbrains.handson.kmm.shared.data.WriteMemoDataTypeList
import com.jetbrains.handson.kmm.shared.entity.AsyncWeatherInfoState
import com.jetbrains.handson.kmm.shared.entity.CURRENTLOCATION_TBL
import com.jetbrains.handson.kmm.shared.entity.DrawingPolyline
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
import com.jetbrains.handson.kmm.shared.entity.MEMO_FILE_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TAG_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_TEXT_TBL
import com.jetbrains.handson.kmm.shared.entity.MEMO_WEATHER_TBL
import com.jetbrains.handson.kmm.shared.entity.toCURRENTWEATHER_TBL
import com.jetbrains.handson.kmm.shared.network.GisMemoApi
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

    val currentSelectedTab: MutableStateFlow<WriteMemoDataType?>
            = MutableStateFlow(null)


    val _currentWeatherStateFlow: MutableStateFlow<AsyncWeatherInfoState>
            = MutableStateFlow( AsyncWeatherInfoState.Empty)


    val currentAudioText: MutableStateFlow<List<Pair<String, List<String>>>>
            = MutableStateFlow( listOf())

    val currentPhoto:  MutableStateFlow<List<String>>
            = MutableStateFlow( listOf())

    val currentVideo: MutableStateFlow<List<String>>
            = MutableStateFlow( listOf())

    val currentSnapShot: MutableStateFlow<List<String>>
            = MutableStateFlow( emptyList())

    val _markerMemoList:MutableStateFlow<List<MEMO_TBL>>
            = MutableStateFlow(listOf())

    val selectedMemo:MutableStateFlow<MEMO_TBL?>
            = MutableStateFlow(null)


    var selectedTagList: MutableStateFlow<ArrayList<Int>>
            = MutableStateFlow(arrayListOf())

    val selectedWeather:MutableStateFlow<MEMO_WEATHER_TBL?>
        = MutableStateFlow(null)

    val detailAudioText: MutableStateFlow<List<Pair<String, List<String>>>>
            = MutableStateFlow( listOf())

    val detailPhoto:  MutableStateFlow<List<String>>
            = MutableStateFlow( listOf())

    val detailVideo: MutableStateFlow<List<String>>
            = MutableStateFlow( listOf())

    val detailSnapShot: MutableStateFlow<List<String>>
            = MutableStateFlow( listOf())


    val currentIsDrawing: MutableStateFlow<Boolean>
            = MutableStateFlow(false)

    val currentIsEraser: MutableStateFlow<Boolean>
            = MutableStateFlow(false)


    val currentIsLock: MutableStateFlow<Boolean>
            = MutableStateFlow(false)

    val currentIsMarker: MutableStateFlow<Boolean>
            = MutableStateFlow(false)


    val currentPolylineList:MutableStateFlow<List<DrawingPolyline>>
        = MutableStateFlow(listOf())


    fun setSelectedTab(data: WriteMemoDataType){
        currentSelectedTab.value = data
    }

    fun updateCurrentIsDrawing(isDrawing:Boolean){
        currentIsDrawing.value = isDrawing
    }

    fun updateCurrentIsEraser(isEraser:Boolean){
        currentIsEraser.value = isEraser
    }

    fun updateCurrentIsLock(isLock:Boolean){
        currentIsLock.value = isLock
    }

    fun updateCurrentIsMarker(isMarker:Boolean){
        currentIsMarker.value = isMarker
    }

    fun clearCurrentValue(){
        selectedTagList.value = arrayListOf()

        currentIsLock.value = false
        currentIsMarker.value = false
        currentIsDrawing.value = false
        currentIsEraser.value = false
        currentPolylineList.value = listOf()
        currentSnapShot.value = listOf()
    }

    fun setAudioText(audioTextList: List<Pair<String, List<String>>>){
        currentAudioText.value = audioTextList
    }

    fun setPhotoVideo(photoList:List<String>, videoList:List<String>){
        currentPhoto.value = photoList
        currentVideo.value = videoList
    }

    fun setSnapShot(data:List<String> ){
          currentSnapShot.value = data
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

        val snapshot = currentSnapShot.value.first()

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
                    filePath = uri
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
                    filePath = uri
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
                    filePath = uri
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
                        filePath = uri
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

        gisMemoDao.selectCurrentWeatherFlow.collectLatest {
            it?.let {
                gisMemoDao.insertMemoWeather(it)
            }
        }
        /*
        _currentWeather.value?.let {
            gisMemoDao.insertMemoWeather(it)
        }
         */

        initMemoItem()


    }

    suspend fun initMemoItem(){

        WriteMemoDataTypeList.forEach {

            when(it){
                WriteMemoDataType.PHOTO -> {
                    val newMemoItem = currentPhoto.value.toMutableList()
                    newMemoItem.clear()
                 //   currentPhoto.emit(newMemoItem)
                    currentPhoto.value = newMemoItem
                }

                WriteMemoDataType.AUDIOTEXT -> {
                    val newMemoItem = currentAudioText.value.toMutableList()
                    newMemoItem.clear()
                  //  currentAudioText.emit(newMemoItem)
                    currentAudioText.value = newMemoItem
                }
                WriteMemoDataType.VIDEO -> {
                    val newMemoItem = currentVideo.value.toMutableList()
                    newMemoItem.clear()
                  //  currentVideo.emit(newMemoItem)
                    currentVideo.value = newMemoItem
                }
                WriteMemoDataType.SNAPSHOT -> {
                    val newMemoItem = currentSnapShot.value.toMutableList()
                    newMemoItem.clear()
                 //   currentSnapShot.emit(newMemoItem)
                    currentSnapShot.value = newMemoItem
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

    fun getShareMemoData(   id:Long, completeHandle:(attachments:ArrayList<String>,  comments:ArrayList<String>)->Unit )
            = CoroutineScope(Dispatchers.IO).launch {
        val attachments = arrayListOf<String>()
        val comments = arrayListOf<String>()


        gisMemoDao.selectMemoFile(id).forEach {
            attachments.add(it.filePath)
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

        gisMemoDao.selectMemoFileListFlow(id).collectLatest { it ->
            val currentSnapShotList = it.filter {
                it.type ==  WriteMemoDataType.SNAPSHOT.name
            }.sortedBy {
                it.index
            }.map {
                it.filePath
            }

            detailSnapShot.emit( currentSnapShotList )

            val currentPhotoList = it.filter {
                it.type ==  WriteMemoDataType.PHOTO.name
            }.sortedBy {
                it.index
            }.map {
                it.filePath
            }

            detailPhoto.emit(  currentPhotoList )

            val currentVideoList = it.filter {
                it.type == WriteMemoDataType.VIDEO.name
            }.sortedBy {
                it.index
            }.map {
                it.filePath
            }

            detailVideo.emit(  currentVideoList  )

            gisMemoDao.selectMemoTextListFlow(id).collectLatest {memoTextTblList ->

                val audiTextList = mutableListOf<Pair<String,List<String>>>()
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
                                it.filePath
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

    suspend fun getWeatherData(
            lat:String,
            lon:String,
            appid:String,
            units:String = "metric"
    ) {
        try {
            api.getWeatherData(
                lat = lat,
                lon = lon,
                units = units,
                appid = appid
            ).toCURRENTWEATHER_TBL().also {
                gisMemoDao.insertCurrentWeather(it)
                _currentWeatherStateFlow.value = AsyncWeatherInfoState.Success(it)
            }
        }catch(e:Exception){
            _currentWeatherStateFlow.value = AsyncWeatherInfoState.Error(e.message?: "Unknown Error")
        }

    }


    suspend fun setWeatherInfo(){
        gisMemoDao.selectCurrentWeatherFlow.collectLatest {
            if(it == null){
                _currentWeatherStateFlow.value = AsyncWeatherInfoState.Empty
            }else {
                _currentWeatherStateFlow.value = AsyncWeatherInfoState.Success(it)
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
