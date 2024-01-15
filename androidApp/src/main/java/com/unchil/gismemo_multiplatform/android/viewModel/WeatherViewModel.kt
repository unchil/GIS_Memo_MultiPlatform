package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.entity.AsyncWeatherInfoState
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
import com.unchil.gismemo_multiplatform.android.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherViewModel(val repository:GisMemoRepository) : ViewModel() {

    private val _currentWeatherStateFlow: MutableStateFlow<AsyncWeatherInfoState>
        = MutableStateFlow( AsyncWeatherInfoState.Loading)

    val currentWeatherStateFlow: StateFlow<AsyncWeatherInfoState>
        = _currentWeatherStateFlow

    init {
        connectWeatherInfoStream()
    }

    fun onEvent(event: Event){
        when(event){
            is Event.SearchWeather -> {
                searchWeather(event.location)
            }
        }
    }

    private fun connectWeatherInfoStream(){
        viewModelScope.launch {
            repository.setWeatherInfo()
            repository._currentWeatherStateFlow.collectLatest {
                _currentWeatherStateFlow.value = it
            }
        }
    }

    private fun searchWeather(location: LatLngAlt) {
        viewModelScope.launch {
            val result = repository.getWeatherData(
                location.latitude.toString(),
                location.longitude.toString(),
                appid = BuildConfig.OPENWEATHER_KEY
            )
        }
    }

    sealed class  Event{
        data class SearchWeather (val location: LatLngAlt) :Event()
    }



}