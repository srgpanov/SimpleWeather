package com.srgpanov.simpleweather.ui.pager_screen

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View

class LeftEdgeScrollTouchListener : View.OnTouchListener {
    var canScroll: Boolean = false
    var isEnabled = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        Log.d("LeftEdgeScrollTouch", "onTouch: ${event.x}")
        if (!isEnabled) return false
        if (event.action == MotionEvent.ACTION_MOVE) {
            return !canScroll
        }
        return false
    }
}