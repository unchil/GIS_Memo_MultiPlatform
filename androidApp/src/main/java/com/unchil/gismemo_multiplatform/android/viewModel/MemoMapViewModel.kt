package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MemoMapViewModel  (val repository: GisMemoRepository) : ViewModel() {


    private val _markerMemoList: MutableStateFlow<List<MEMO_TBL>>
        = MutableStateFlow(emptyList())

    val markerMemoList:StateFlow<List<MEMO_TBL>>
        = _markerMemoList.asStateFlow()

    init {
        viewModelScope.launch {
            repository._markerMemoList.collectLatest {
                _markerMemoList.value = it
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.SetMarkers -> setMarkers()
            is Event.ToRoute -> toRoute(event.navController, event.route)
        }
    }

    private fun toRoute(navController: NavHostController, route:String){
        navController.navigate(route = route)
    }

    private fun setMarkers(){
        viewModelScope.launch {
            repository.setMarkerMemoList()
        }
    }

    sealed class Event {
        object  SetMarkers:Event()
        data class ToRoute(val navController: NavHostController, val route:String) : Event()
    }
}