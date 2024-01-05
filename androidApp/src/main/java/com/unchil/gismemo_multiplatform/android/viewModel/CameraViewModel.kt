package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CameraViewModel (val repository: GisMemoRepository) : ViewModel() {

    var _currentPhoto: MutableList<Url> = mutableListOf()
    var _currentVideo: MutableList<Url> = mutableListOf()

    init {
        _currentPhoto  = repository.currentPhoto.value.toMutableList()
        _currentVideo = repository.currentVideo.value.toMutableList()
    }


    fun onEvent(event: Event){
        when(event){
            is Event.SetPhotoVideo -> {
                setPhotoVideo(event.photoList, event.videoList)
            }

            else -> {}
        }
    }


    private fun setPhotoVideo(photoList:List<Url>, videoList:List<Url>){
        viewModelScope.launch {
            repository.setPhotoVideo(photoList, videoList)
        }
    }


    sealed class Event {
        data class SetPhotoVideo(val photoList:List<Url>, val videoList:List<Url>):Event()
    }

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect

    sealed class Effect {
        object NoAction: Effect()
    }


}