package com.jetbrains.handson.kmm.shared

//ios Test

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.CombinedLoadStates
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.jetbrains.handson.kmm.shared.entity.MEMO_TBL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import platform.UIKit.UICollectionView
import platform.darwin.NSInteger

// Making abstract causes the compilation error "Non-final Kotlin subclasses of Objective-C classes are not yet supported".
class PagingCollectionViewController< MEMO_TBL : Any> {

    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    private val workerDispatcher: CoroutineDispatcher = Dispatchers.Default
    private var collectionView: UICollectionView? = null

    private object diffCallback : DiffUtil.ItemCallback<MEMO_TBL>() {
        override fun areItemsTheSame(oldItem: MEMO_TBL, newItem: MEMO_TBL): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MEMO_TBL, newItem: MEMO_TBL): Boolean {
            return oldItem == newItem
        }
    }

    private object updateCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            TODO("onChanged(position=$position, count=$count, payload=$payload)")
        }

        override fun onInserted(position: Int, count: Int) {
            TODO("Not yet implemented")
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            TODO("Not yet implemented")
        }

        override fun onRemoved(position: Int, count: Int) {
            TODO("Not yet implemented")
        }
    }

    private val differ = AsyncPagingDataDiffer(
        diffCallback = diffCallback,
        updateCallback = updateCallback,
        mainDispatcher = mainDispatcher,
        workerDispatcher = workerDispatcher,
    )

    suspend fun submitData(pagingData: PagingData<MEMO_TBL>) {
        differ.submitData(pagingData)
    }

    fun retry() {
        differ.retry()
    }

    fun refresh() {
        differ.refresh()
    }

    protected fun getItem(position: Int) = differ.getItem(position)

    fun peek(index: Int) = differ.peek(index)

    fun snapshot(): ItemSnapshotList<com.jetbrains.handson.kmm.shared.entity.MEMO_TBL> = differ.snapshot()

    fun collectionView(collectionView: UICollectionView, numberOfItemsInSection: NSInteger): NSInteger {
        this.collectionView = collectionView
        return differ.itemCount.toLong()
    }

    val loadStateFlow: Flow<CombinedLoadStates> = differ.loadStateFlow

    val onPagesUpdatedFlow: Flow<Unit> = differ.onPagesUpdatedFlow

    fun addLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.addLoadStateListener(listener)
    }

    fun removeLoadStateListener(listener: (CombinedLoadStates) -> Unit) {
        differ.removeLoadStateListener(listener)
    }

    fun addOnPagesUpdatedListener(listener: () -> Unit) {
        differ.addOnPagesUpdatedListener(listener)
    }

    fun removeOnPagesUpdatedListener(listener: () -> Unit) {
        differ.removeOnPagesUpdatedListener(listener)
    }
}

private fun <T : Any, MEMO_TBL : Any> AsyncPagingDataDiffer<T>.submitData(pagingData: PagingData<MEMO_TBL>) {

}



