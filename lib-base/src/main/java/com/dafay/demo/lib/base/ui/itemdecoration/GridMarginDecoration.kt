package com.example.demo.biz.base.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridMarginDecoration : RecyclerView.ItemDecoration {

    private val left: Int
    private val top: Int
    private val right: Int
    private val bottom: Int

    constructor(left: Int, top: Int, right: Int, bottom: Int) : super() {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(left, top, right, bottom)
    }
}