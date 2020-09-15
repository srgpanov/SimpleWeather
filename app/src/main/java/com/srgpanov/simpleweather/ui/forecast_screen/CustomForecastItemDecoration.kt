package com.srgpanov.simpleweather.ui.forecast_screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.other.getDrawableCompat

class CustomForecastItemDecoration(
    context: Context
) : RecyclerView.ItemDecoration() {
    val drawable: Drawable = context.getDrawableCompat(R.drawable.custom_divider_2dp)!!
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.adapter?.itemCount ?: 0
        for (i in 0 until childCount -1) {
            val child = parent.getChildAt(i)
            if (child != null) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + drawable.intrinsicHeight
                drawable.setBounds(left, top, right, bottom)
                drawable.draw(c)
            }
        }
    }
}