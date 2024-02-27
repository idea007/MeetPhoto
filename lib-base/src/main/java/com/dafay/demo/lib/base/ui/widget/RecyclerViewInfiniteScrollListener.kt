package com.dafay.demo.lib.base.ui.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * A scroll listener for RecyclerView to load more items as you approach the end.
 * Adapted from https://gist.github.com/ssinss/e06f12ef66c51252563e
 *
 * RecyclerView 滑到底部监听
 */
abstract class RecyclerViewInfiniteScrollListener(private val layoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {
    private val loadMoreRunnable = Runnable { onLoadMore() }

    /**
     * 最后一个的位置
     */
    private var fristPositions: IntArray? = null

    private var visibleItemCount: Int = 0
    private var totalItemCount = 0
    private var firstVisibleItem = 0


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        // bail out if scrolling upward or already loading data
        if (dy < 0) return
        if (layoutManager is LinearLayoutManager) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
                recyclerView.post(loadMoreRunnable)
            }
        } else if (layoutManager is GridLayoutManager) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
                recyclerView.post(loadMoreRunnable)
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            if (fristPositions == null) {
                fristPositions = IntArray(layoutManager.spanCount)
            }
            layoutManager.findFirstVisibleItemPositions(fristPositions)
            firstVisibleItem = findMin(fristPositions!!)
            if (totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
                recyclerView.post(loadMoreRunnable)
            }
        } else {
            throw RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }

    }

    private fun findMin(lastPositions: IntArray): Int {
        var min = lastPositions[0]
        for (value in lastPositions) {
            if (value < min) {
                min = value
            }
        }
        return min
    }

    abstract fun onLoadMore()

    companion object {
        // The minimum number of items remaining before we should loading more.
        private const val VISIBLE_THRESHOLD = 5
    }
}
