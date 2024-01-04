package com.unchil.gismemo_multiplatform.android.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.entity.LatLngAlt
import com.jetbrains.handson.kmm.shared.entity.RecvWeatherDataState
import com.unchil.gismemo_multiplatform.android.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherViewModel(val repository:GisMemoRepository) : ViewModel() {


    val _currentWeatheStaterFlow: MutableStateFlow<RecvWeatherDataState>
            = MutableStateFlow( RecvWeatherDataState.Loading)

    val currentWeatheStaterFlow: StateFlow<RecvWeatherDataState> = _currentWeatheStaterFlow

    fun onEvent(event: Event){
        when(event){
            is Event.SearchWeather -> {
                searchWeather(event.location)
            }

            Event.GetWeather -> {
                getWeather()
            }
        }
    }

     fun getWeather(){
         viewModelScope.launch {
             repository.getWeatherFlow.collectLatest {
                it?.let {
                    _currentWeatheStaterFlow.value = RecvWeatherDataState.Success(it)
                }
             }
         }
    }

    @SuppressLint("SuspiciousIndentation")
    fun searchWeather(location: LatLngAlt) {
        _currentWeatheStaterFlow.value = RecvWeatherDataState.Loading

        viewModelScope.launch {

            val result = repository.getWeatherData(
                location.latitude.toString(),
                location.longitude.toString(),
                appid = BuildConfig.OPENWEATHER_KEY
            )

            _currentWeatheStaterFlow.value = result

                when(result){
                    is RecvWeatherDataState.Error -> {
                        getWeather()
                    }
                    else -> {}
                }

        }
    }




    sealed class  Event{
        data class SearchWeather (val location: LatLngAlt) :Event()

        object GetWeather: Event()
    }



}