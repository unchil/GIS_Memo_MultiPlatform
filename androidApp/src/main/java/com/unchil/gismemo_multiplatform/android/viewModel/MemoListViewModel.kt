package com.unchil.gismemo_multiplatform.android.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jetbrains.handson.kmm.shared.GisMemoRepository
import com.jetbrains.handson.kmm.shared.data.SearchQueryData
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MemoListViewModel (val repository: GisMemoRepository) : ViewModel() {

    /*
    private val _isRefreshingStateFlow: MutableStateFlow<Boolean>
        = MutableStateFlow(false)

    val isRefreshingStateFlow: StateFlow<Boolean>
        = _isRefreshingStateFlow.asStateFlow()

     */

    val memoPagingStream : Flow<PagingData<MEMO_TBL>>
    private val searchQueryFlow:Flow<Event.Search>
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
                emit( Event.Search(queryData = SearchQueryData) )
            }

        //  3.  viewModelScope.launch { searchMemo() }
        memoPagingStream = searchQueryFlow
            .flatMapLatest {
                searchMemo(queryData = it.queryData)
            }
            .cachedIn(viewModelScope)
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
            is Event.ToRoute -> {
                toRoute(event.navController, event.route)
            }

            else -> {}
        }
    }

    private fun toRoute(navController: NavHostController, route:String){
        navController.navigate( route = route)
    }

    private fun deleteItem(id:Long){
        viewModelScope.launch {
            repository.deleteMemo(id= id)
        }
    }

    private fun searchMemo(queryData: SearchQueryData): Flow<PagingData<MEMO_TBL>> {
        return repository.memoPagingStream(queryData = SearchQueryData)
    }



    sealed class Event {
        data class Search(val queryData:SearchQueryData) : Event()

        data class ToRoute(val navController: NavHostController, val route:String) : Event()

        data class DeleteItem(val id:Long):Event()
    }
}