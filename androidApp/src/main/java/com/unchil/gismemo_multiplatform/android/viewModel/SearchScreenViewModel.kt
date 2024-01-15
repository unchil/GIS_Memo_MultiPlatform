package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import com.unchil.gismemo_multiplatform.android.model.QueryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class SearchScreenViewModel (val repository: GisMemoRepository) : ViewModel() {

    /*
    private val _isRefreshingStateFlow: MutableStateFlow<Boolean>
        = MutableStateFlow(false)

    val isRefreshingStateFlow: StateFlow<Boolean>
        = _isRefreshingStateFlow.asStateFlow()

     */

    val memoPagingStream : Flow<PagingData<MEMO_TBL>>
    val searchQueryFlow:Flow<Event.Search>
    val eventHandler: (Event) -> Unit

    init {
        val eventStateFlow = MutableSharedFlow<Event>()

        //  1. fun onEvent(event: Event)
        eventHandler = {
            viewModelScope.launch {
                eventStateFlow.emit(it)
            }
        }

        //  2.   when (event) { is Event.Search ->   }
        searchQueryFlow = eventStateFlow
            .filterIsInstance<Event.Search>()
            .distinctUntilChanged()
            .onStart {
                emit( Event.Search(queryDataList = mutableListOf()) )
            }

        //  3.  viewModelScope.launch { searchMemo() }
        memoPagingStream = searchQueryFlow
            .flatMapLatest {
                searchMemo(queryDataList = it.queryDataList)
            }.cachedIn(viewModelScope)
        /*
        cachedIn(viewModelScope)
        A common use case for this caching is to cache PagingData in a ViewModel.
        This can ensure that, upon configuration change (e.g. rotation),
        then new Activity will receive the existing data immediately
        rather than fetching it from scratch.
         */

    }

    fun onEvent(event: Event) {
        when(event){
            is Event.DeleteItem -> {
                deleteItem(event.id)
            }
            else -> {}
        }
    }

    private fun deleteItem(id:Long){
        viewModelScope.launch {
            repository.deleteMemo(id= id)
        }
    }

    private fun searchMemo(queryDataList:MutableList<QueryData>): Flow<PagingData<MEMO_TBL>> {
        return  repository.getMemoListPagingFlow
    }

    sealed class Event {
        data class Search(val queryDataList:MutableList<QueryData>) : Event()

        data class DeleteItem(val id:Long):Event()
    }
}