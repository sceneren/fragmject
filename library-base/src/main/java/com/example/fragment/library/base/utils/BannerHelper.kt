package com.example.fragment.library.base.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BannerHelper(
    private val recyclerView: RecyclerView,
    @RecyclerView.Orientation
    private val orientation: Int = RecyclerView.HORIZONTAL
) {

    private val layoutManager = RepeatLayoutManager(recyclerView.context)
    private var listener: OnItemScrollListener? = null
    private var isDragging = false
    private val bannerTask = Runnable {
        offsetItem()
        start()
    }

    init {
        layoutManager.orientation = orientation
        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isDragging) {
                        isDragging = false
                        offsetItem()
                        start()
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isDragging = true
                    stop()
                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    stop()
                }
            }
        })
    }

    private fun offsetItem() {
        val position = layoutManager.findFirstVisibleItemPosition()
        val itemView = layoutManager.findViewByPosition(position) ?: return
        if (orientation == RecyclerView.HORIZONTAL) {
            val lmWidth = layoutManager.width
            var dx = if (itemView.width < lmWidth / 2) {
                itemView.right
            } else {
                val offset = (lmWidth - itemView.width) / 2
                if (itemView.right < lmWidth / 2) {
                    itemView.right - offset
                } else {
                    offset - (lmWidth - itemView.right)
                }
            }
            if (dx == 0) {
                dx = itemView.width
            }
            recyclerView.smoothScrollBy(dx, 0)
        } else {
            val lmHeight = layoutManager.height
            var dy = if (itemView.height < lmHeight / 2) {
                itemView.bottom
            } else {
                val offset = (lmHeight - itemView.height) / 2
                if (itemView.bottom < lmHeight / 2) {
                    itemView.bottom - offset
                } else {
                    offset - (lmHeight - itemView.bottom)
                }
            }
            if (dy == 0) {
                dy = itemView.height
            }
            recyclerView.smoothScrollBy(0, dy)
        }
        recyclerView.post {
            listener?.onItemScroll(findItemPosition())
        }
    }

    fun findItemPosition(): Int {
        return layoutManager.findLastCompletelyVisibleItemPosition()
    }

    fun start() {
        recyclerView.removeCallbacks(bannerTask)
        recyclerView.postDelayed(bannerTask, 5000)
    }

    fun stop() {
        recyclerView.removeCallbacks(bannerTask)
    }

    fun setOnItemScrollListener(listener: OnItemScrollListener) {
        this.listener = listener
    }

}

interface OnItemScrollListener {
    fun onItemScroll(position: Int)
}

/**
 * 修改自jiarWang的RepeatLayoutManager
 * https://github.com/jiarWang/RepeatLayoutManager
 */
open class RepeatLayoutManager(val context: Context) : LinearLayoutManager(context) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount <= 0 || state.isPreLayout) return
        val position = findFirstVisibleItemPosition()
        val itemView = findViewByPosition(position) ?: return
        if (orientation == RecyclerView.HORIZONTAL) {
            fillHorizontal(itemView.width * itemCount, recycler)
            if (itemView.width > width / 2) {
                val offset = (width - itemView.width) / 2
                val dx = itemView.right - offset
                scrollHorizontallyBy(dx, recycler, state)
            }
        } else {
            fillVertical(itemView.height * itemCount, recycler)
            if (itemView.height > height / 2) {
                val offset = (height - itemView.height) / 2
                val dy = itemView.bottom - offset
                scrollVerticallyBy(dy, recycler, state)
            }
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        fillHorizontal(dx, recycler)
        offsetChildrenHorizontal(-dx)
        recyclerChildView(dx > 0, recycler)
        return dx
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        fillVertical(dy, recycler)
        offsetChildrenVertical(-dy)
        recyclerChildView(dy > 0, recycler)
        return dy
    }

    private fun fillHorizontal(dx: Int, recycler: RecyclerView.Recycler) {
        if (childCount == 0) return
        if (dx > 0) {
            var anchorView = getChildAt(childCount - 1) ?: return
            while (anchorView.right < width - paddingRight + dx) {
                val anchorPosition = getPosition(anchorView)
                var position = (anchorPosition + 1) % itemCount
                if (position < 0) position += itemCount
                val scrapView = recycler.getViewForPosition(position)
                addView(scrapView)
                measureChildWithMargins(scrapView, 0, 0)
                val left = anchorView.right
                val right = left + getDecoratedMeasuredWidth(scrapView)
                val top = paddingTop
                val bottom = top + getDecoratedMeasuredHeight(scrapView)
                layoutDecorated(scrapView, left, top, right, bottom)
                anchorView = scrapView
            }
        } else {
            var anchorView = getChildAt(0) ?: return
            while (anchorView.left > paddingLeft + dx) {
                val anchorPosition = getPosition(anchorView)
                var position = (anchorPosition - 1) % itemCount
                if (position < 0) position += itemCount
                val scrapView = recycler.getViewForPosition(position)
                addView(scrapView, 0)
                measureChildWithMargins(scrapView, 0, 0)
                val right = anchorView.left
                val left = right - getDecoratedMeasuredWidth(scrapView)
                val top = paddingTop
                val bottom = top + getDecoratedMeasuredHeight(scrapView)
                layoutDecorated(scrapView, left, top, right, bottom)
                anchorView = scrapView
            }
        }
    }

    private fun fillVertical(dy: Int, recycler: RecyclerView.Recycler) {
        if (childCount == 0) return
        if (dy > 0) {
            //填充尾部
            var anchorView = getChildAt(childCount - 1) ?: return
            while (anchorView.bottom < height - paddingBottom + dy) {
                val anchorPosition = getPosition(anchorView)
                var position = (anchorPosition + 1) % itemCount
                if (position < 0) position += itemCount
                val scrapView = recycler.getViewForPosition(position)
                addView(scrapView)
                measureChildWithMargins(scrapView, 0, 0)
                val left = paddingLeft
                val right = left + getDecoratedMeasuredWidth(scrapView)
                val top = anchorView.bottom
                val bottom = top + getDecoratedMeasuredHeight(scrapView)
                layoutDecorated(scrapView, left, top, right, bottom)
                anchorView = scrapView
            }
        } else {
            //填充头部
            var anchorView = getChildAt(0) ?: return
            while (anchorView.top > paddingTop + dy) {
                val anchorPosition = getPosition(anchorView)
                var position = (anchorPosition - 1) % itemCount
                if (position < 0) position += itemCount
                val scrapView = recycler.getViewForPosition(position)
                addView(scrapView, 0)
                measureChildWithMargins(scrapView, 0, 0)
                val left = paddingLeft
                val right = left + getDecoratedMeasuredWidth(scrapView)
                val bottom = anchorView.top
                val top = bottom - getDecoratedMeasuredHeight(scrapView)
                layoutDecorated(scrapView, left, top, right, bottom)
                anchorView = scrapView
            }
        }
    }

    /**
     * 回收界面不可见的view
     */
    private fun recyclerChildView(
        fillEnd: Boolean,
        recycler: RecyclerView.Recycler
    ) {
        if (fillEnd) {
            //回收头部
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: return
                val needRecycler = if (orientation == RecyclerView.HORIZONTAL)
                    view.right < paddingLeft
                else
                    view.bottom < paddingTop
                if (!needRecycler) return
                removeAndRecycleView(view, recycler)
            }
        } else {
            //回收尾部
            for (i in childCount - 1 downTo 0) {
                val view = getChildAt(i) ?: return
                val needRecycler = if (orientation == RecyclerView.HORIZONTAL)
                    view.left > width - paddingRight
                else
                    view.top > height - paddingBottom
                if (!needRecycler) return
                removeAndRecycleView(view, recycler)
            }
        }
    }

}