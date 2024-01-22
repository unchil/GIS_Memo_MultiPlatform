package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import kotlinx.coroutines.launch

class SettingViewModel  (val repository: GisMemoRepository) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is Event.UpdateIsUsableHaptic -> {
                updateIsUsableHaptic(event.isUsable)
            }
            is Event.UpdateIsUsableDarkMode -> {
                updateIsUsableDarkMode(event.isUsableDarkMode)
            }
            is Event.UpdateIsDynamicColor -> {
                updateIsDynamicColor(event.isUsableDynamicColor)
            }
            is Event.UpdateOnChangeLocale -> {
                updateOnChangeLocale(event.onChnageLocale)
            }
            is Event.UpdateIsChangeLocale -> {
                updateIsChangeLocale(event.isChnageLocale)
            }
            Event.clearAllMemo -> {
                clearAllMemo()
            }
        }
    }

    private fun clearAllMemo () {
        viewModelScope.launch {
            repository.deleteAllMemo()
        }
    }

    private fun updateIsUsableHaptic (isUsable:Boolean) {
        repository.updateIsUsableHaptic(isUsable)
    }

    private fun updateIsUsableDarkMode (isUsableDarkMode:Boolean) {
        repository.updateIsUsableDarkMode(isUsableDarkMode)
    }

    private fun updateIsDynamicColor (isUsableDynamicColor:Boolean) {
        repository.updateIsUsableDynamicColor(isUsableDynamicColor)
    }


    private fun updateOnChangeLocale (onChangeLocale:Boolean) {
        repository.updateOnChangeLocale(onChangeLocale)
    }

    private fun updateIsChangeLocale (isChangeLocale:Int) {
        repository.updateIsChangeLocale(isChangeLocale)
    }

    sealed class Event {
        data class UpdateIsUsableHaptic(val isUsable:Boolean): Event()

        data class UpdateIsUsableDarkMode(val isUsableDarkMode:Boolean): Event()

        data class UpdateIsDynamicColor(val isUsableDynamicColor:Boolean): Event()

        data class UpdateOnChangeLocale(val onChnageLocale:Boolean): Event()

        data class UpdateIsChangeLocale(val isChnageLocale:Int): Event()

        object clearAllMemo: Event()
    }
}