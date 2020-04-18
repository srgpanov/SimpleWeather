package com.srgpanov.simpleweather.ui.pager_screen

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.srgpanov.simpleweather.other.dpToPx


class EdgePager @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    ViewPager(context, attributeSet) {
    var startX = 0f
    var edgeDistance = dpToPx(32)
    var inLeftEdge = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (currentItem == 1) {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = ev.x
                }
            }
            inLeftEdge = startX < edgeDistance
            return if (inLeftEdge) {
                super.onInterceptTouchEvent(ev)
            } else {
                false
            }
        } else {
            return super.onInterceptTouchEvent(ev)
        }
    }
}