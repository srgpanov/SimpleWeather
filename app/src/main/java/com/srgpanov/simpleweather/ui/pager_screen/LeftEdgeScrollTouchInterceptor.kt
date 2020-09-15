package com.srgpanov.simpleweather.ui.pager_screen

import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.other.dpToPx

class LeftEdgeScrollTouchInterceptor : RecyclerView.SimpleOnItemTouchListener() {
    private var inLeftEdge = true
    private var startX = 0f
    private var edgeDistance = dpToPx(32)
    var canScrollListener: ((Boolean) -> Unit)? = null

    override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
        Log.d("LeftEdgeScroll", "onInterceptTouchEvent: ${event.x}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                inLeftEdge = startX < edgeDistance
                canScrollListener?.invoke(inLeftEdge)
                return false
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                inLeftEdge = true
                canScrollListener?.invoke(inLeftEdge)
                return false
            }
        }
        return false
    }


}