package com.srgpanov.simpleweather.ui.forecast_screen

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.other.FirstItemCompletelyVisibleListener

class ForecastLayoutManager(
    context: Context
) : LinearLayoutManager(context) {
    var itemVisibleListener: FirstItemCompletelyVisibleListener? = null

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        // triggered when there's a re-layout (item added/removed etc)
        val visibility = findFirstCompletelyVisibleItemPosition() == 0
        itemVisibleListener?.isVisible(visibility)
    }

    override fun offsetChildrenVertical(dy: Int) {
        super.offsetChildrenVertical(dy)
        // triggered during scroll
        val visibility = findFirstCompletelyVisibleItemPosition() == 0
        itemVisibleListener?.isVisible(visibility)
    }
}