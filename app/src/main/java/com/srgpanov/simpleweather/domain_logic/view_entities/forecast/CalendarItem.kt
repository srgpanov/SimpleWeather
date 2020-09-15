package com.srgpanov.simpleweather.domain_logic.view_entities.forecast

import android.graphics.Color.WHITE
import android.graphics.Color.parseColor
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.annotation.ColorInt
import com.srgpanov.simpleweather.databinding.CalendarDayItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem

data class CalendarItem(
    val dayText: String,
    val numberText: String,
    val isSelected: Boolean = false
) : ViewItem<CalendarDayItemBinding> {
    private val circleVisibility: Int =
        if (isSelected) VISIBLE else INVISIBLE

    @ColorInt
    private val textColor: Int =
        if (isSelected) WHITE else parseColor("#212121")

    override fun bind(binding: CalendarDayItemBinding) {
        binding.day.text = dayText
        binding.numbers.text = numberText
        binding.circle.visibility = circleVisibility
        binding.numbers.setTextColor(textColor)
    }
}