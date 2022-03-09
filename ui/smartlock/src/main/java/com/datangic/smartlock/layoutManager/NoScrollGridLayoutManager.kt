package com.datangic.smartlock.layoutManager

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager


internal class NoScrollGridLayoutManager: GridLayoutManager {
    private var scrollHorizontally = true
    private var scrollVertically = true

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context?, spanCount: Int) : super(context, spanCount)
    constructor(context: Context?, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(context, spanCount, orientation, reverseLayout)

    fun setScrollEnabled(flag: Boolean) {
        scrollHorizontally = flag
        scrollVertically = flag
    }

    override fun canScrollHorizontally(): Boolean {
        return scrollHorizontally && super.canScrollHorizontally()
    }

    override fun canScrollVertically(): Boolean {
        return scrollVertically && super.canScrollVertically()
    }
}