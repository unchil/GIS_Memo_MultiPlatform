package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.unchil.gismemo_multiplatform.android.model.MemoDataUser
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MemoDataViewModel  (val repository: GisMemoRepository, val user:MemoDataUser) : ViewModel() {

    private val _photoListStateFlow: MutableStateFlow<List<String>>
            = MutableStateFlow(emptyList())

    val photoListStateFlow: StateFlow<List<String>>
            = _photoListStateFlow


    private val _videoListStateFlow: MutableStateFlow<List<String>>
            = MutableStateFlow(emptyList())

    val videoListStateFlow:  StateFlow<List<String>>
            = _videoListStateFlow

    private val _audioTextStateFlow: MutableStateFlow<List<Pair<String, List<String>>>>
            = MutableStateFlow(emptyList())

    val audioTextStateFlow: StateFlow<List<Pair<String, List<String>>>>
            = _audioTextStateFlow


    private val  _snapShotListStateFlow: MutableStateFlow<List<String>>
            = MutableStateFlow(emptyList())

    val snapShotListStateFlow:  StateFlow<List<String>>
            = _snapShotListStateFlow


    init {
        when(user){
            MemoDataUser.DetailMemoView -> {
                /*
                photoListStateFlow  = repository.detailPhoto
                videoListStateFlow  = repository.detailVideo
                audioTextStateFlow  = repository.detailAudioText
                snapShotListStateFlow  = repository.detailSnapShot

                 */

                viewModelScope.launch {
                    repository.detailVideo.collect { it ->
                        _videoListStateFlow.value = it
                    }
                }

                viewModelScope.launch {
                    repository.detailAudioText.collect { it ->
                        _audioTextStateFlow.value = it
                    }
                }

                viewModelScope.launch {
                    repository.detailPhoto.collect { it ->
                        _photoListStateFlow.value = it
                    }
                }

                viewModelScope.launch {
                    repository.detailSnapShot.collect { it ->
                        _snapShotListStateFlow.value = it
                    }
                }
            }
            MemoDataUser.WriteMemoView -> {
                /*
                photoListStateFlow = repository.currentPhoto
                videoListStateFlow = repository.currentVideo
                audioTextStateFlow = repository.currentAudioText
                snapShotListStateFlow = repository.currentSnapShot

                 */

                viewModelScope.launch {
                    repository.currentVideo.collect { it ->
                        _videoListStateFlow.value = it
                    }
                }

                viewModelScope.launch {
                    repository.currentAudioText.collect { it ->
                        _audioTextStateFlow.value = it
                    }
                }

                viewModelScope.launch {
                    repository.currentPhoto.collect { it ->
                        _photoListStateFlow.value = it
                    }
                }

                viewModelScope.launch {
                    repository.currentSnapShot.collect { it ->
                        _snapShotListStateFlow.value = it
                    }
                }




            }
        }
    }


}