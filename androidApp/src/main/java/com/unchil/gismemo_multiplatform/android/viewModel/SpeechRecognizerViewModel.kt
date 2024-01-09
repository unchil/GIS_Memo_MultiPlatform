package com.unchil.gismemo_multiplatform.android.viewModel


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import io.ktor.http.Url
import kotlinx.coroutines.launch

class SpeechRecognizerViewModel (val repository: GisMemoRepository) : ViewModel() {

    var _currentAudioText: MutableList<Pair<String, List<Url>>> = mutableListOf()

    init {
        _currentAudioText = repository.currentAudioText.value.toMutableList()
    }

    fun onEvent(event: Event){
        when(event){
            is Event.SetAudioText -> {
                setAudioText(event.data)
            }
        }
    }

    private fun setAudioText(data: List<Pair<String, List<Url>>>){
        viewModelScope.launch {
            repository.setAudioText(data)
        }
    }


    sealed class Event {
        data class SetAudioText(val data: List<Pair<String,  List<Url>>>):Event()

    }
}