package com.srgpanov.simpleweather.domain_logic.view_entities.favorites

import android.content.Context
import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.FavoriteCurrentItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem

data class CurrentViewItem(
    val title: String,
    @DrawableRes
    val icon: Int?,
    val temp: String?,
    val place: PlaceViewItem?
) : ViewItem<FavoriteCurrentItemBinding> {

    companion object {
        fun emptyCurrent(context: Context): CurrentViewItem {
            return CurrentViewItem(
                title = context.getString(R.string.current_location),
                icon = null,
                temp = context.getString(R.string.current_place_empty_weather_temp),
                place = null
            )
        }
    }

    override fun bind(binding: FavoriteCurrentItemBinding) {
        binding.cityNameTv.text = title
        if (icon != null) {
            binding.cloudnessIv.setImageResource(icon)
        }
        binding.tempValueTv.text = temp
    }
}