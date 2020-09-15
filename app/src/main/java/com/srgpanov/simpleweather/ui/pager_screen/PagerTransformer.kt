package com.srgpanov.simpleweather.ui.pager_screen

import android.graphics.Color
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs


class PagerTransformer : ViewPager2.PageTransformer {
    private val minAlpha = 0.5f
    private var parentView: View? = null
    var color = Color.parseColor("#000000")
    override fun transformPage(page: View, position: Float) {

        page.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    parentView = page.parent as? View
                    parentView?.setBackgroundColor(color)
                    val scaleFactor = (minAlpha + (1 - minAlpha) * (1 - abs(position)))
                    alpha = scaleFactor
                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationZ = -1f

                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }

        }
    }
}
