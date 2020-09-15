package com.srgpanov.simpleweather.ui.pager_screen

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

class LeftEdgeScrollHelper {
    private val touchInterceptor = LeftEdgeScrollTouchInterceptor()
    private val touchListener = LeftEdgeScrollTouchListener()
    private var recyclerView: RecyclerView? = null

    @SuppressLint("ClickableViewAccessibility")
    fun attachToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.addOnItemTouchListener(touchInterceptor)
        recyclerView.setOnTouchListener(touchListener)
        touchInterceptor.canScrollListener = { canScroll ->
            touchListener.canScroll = canScroll
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun detachRecyclerView() {
        recyclerView?.removeOnItemTouchListener(touchInterceptor)
        recyclerView?.setOnTouchListener(null)
        recyclerView = null
    }

    fun switchEnable(enable: Boolean) {
        touchListener.isEnabled = enable
    }
}