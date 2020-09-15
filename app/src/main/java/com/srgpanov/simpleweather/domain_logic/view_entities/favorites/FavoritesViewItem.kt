package com.srgpanov.simpleweather.domain_logic.view_entities.favorites

import androidx.annotation.DrawableRes
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.domain_logic.ViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.getDrawableCompat

data class FavoritesViewItem(
    val title: String,
    @DrawableRes
    val background: Int,
    @DrawableRes
    val icon: Int?,
    val temp: String?,
    val cityTime: String?,
    val place: PlaceViewItem
) : ViewItem<FavoriteLocationItemBinding> {
    override fun bind(binding: FavoriteLocationItemBinding) {
        binding.cityNameTv.text = title
        binding.constraintLayout.background = binding.root.context.getDrawableCompat(background)
        if (icon != null) {
            binding.cloudnessIv.setImageResource(icon)
        }
        binding.tempValueTv.text = temp
        binding.cityTimeTv.text = cityTime
    }

    fun getPayloads(other: FavoritesViewItem?): List<Payload> {
        if (this == other || other == null) return emptyList()
        val payloads = mutableListOf<Payload>()
        if (title != other.title) payloads += Payload.Title(title, other.title)
        if (background != other.background) payloads += Payload.Background(
            background,
            other.background
        )
        if (icon != other.icon) payloads += Payload.Icon(icon, other.icon)
        if (temp != other.temp) payloads += Payload.Temp(temp, other.temp)
        if (cityTime != other.cityTime) payloads += Payload.CityTime(cityTime, other.cityTime)
        return payloads
    }

    sealed class Payload {
        data class Title(val old: String, val new: String) : Payload()
        data class Background(val old: Int, val new: Int) : Payload()
        data class Icon(val old: Int?, val new: Int?) : Payload()
        data class Temp(val old: String?, val new: String?) : Payload()
        data class CityTime(val old: String?, val new: String?) : Payload()
    }
}