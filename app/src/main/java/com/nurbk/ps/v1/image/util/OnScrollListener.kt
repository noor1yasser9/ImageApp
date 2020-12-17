package com.nurbk.ps.v1.image.util

import android.util.Log
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import timber.log.Timber

class OnScrollListener(
    var isLoading: Boolean,
    var isLastPage: Boolean,
    val onComplete: () -> Unit
) : RecyclerView.OnScrollListener() {
    private val TAG = "OnScrollListener"
    private var firstVisibleItemPositions = IntArray(2)
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        Timber.d("$TAG  scrollListener->onScrolled")

        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
        val firstVisibleItemPosition =
            layoutManager.findFirstVisibleItemPositions(firstVisibleItemPositions)[0]
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
        val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
        val isNotAtBeginning = firstVisibleItemPosition >= 0
        val isTotalMoreThenVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
        val shouldPaginate = isNotLoadingAndNotLastPage
                && isAtLastItem
                && isNotAtBeginning && isTotalMoreThenVisible

        Log.e("ttttisAtLastItem","$isAtLastItem")
        Timber.d("$TAG  scrollListener->onScrolled->shouldPaginate $shouldPaginate")
        if (shouldPaginate) {
            onComplete()
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        Timber.d("$TAG  scrollListener->onScrollStateChanged")
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            onComplete()
        }
    }

}