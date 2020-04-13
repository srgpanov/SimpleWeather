package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.formatTemp
import com.srgpanov.simpleweather.other.getWeatherIcon
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesAdapter() : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    var places: MutableList<PlaceEntity> = mutableListOf()
        private set
    var listener: MyClickListener? = null
    var optionsListener: MyClickListener? = null
    var repository: DataRepository? = null
    var scope: CoroutineScope? = null

    companion object {
        const val CURRENT_POSITION = -2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesAdapter.FavoritesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavoritesViewHolder(
            FavoriteLocationItemBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(places[position])
    }


    override fun getItemCount(): Int {
        return places.size
    }


    fun setData(data: List<PlaceEntity>) {
        scope?.launch {
            val oldItems = ArrayList(places)
            val placeDiffCallBack = PlaceDiffCallBack(oldItems, data)

            val resultDiff = DiffUtil.calculateDiff(placeDiffCallBack, false)

            withContext(Dispatchers.Main) {
                resultDiff.dispatchUpdatesTo(this@FavoritesAdapter)
                places.clear()
                places.addAll(data)
            }
        }
    }


    inner class FavoritesViewHolder(private val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceEntity) {
            binding.cityNameTv.text = item.cityTitle
            scope?.launch {
                val weather = repository?.getSimpleWeather(item.toGeoPoint())
                logD("weather adapter ${weather?.forecasts?.size}")
                weather?.let {
                    withContext(Dispatchers.Main) {
                        binding.cloudnessIv.setImageResource(getWeatherIcon(it.fact.icon))
                        binding.tempValueTv.text = formatTemp(it.fact.temp)
                        binding.cityTimeTv.text = it.getLocalTime()
                    }
                }
            }
            binding.constraintLayout.setOnClickListener {
                listener?.onClick(
                    it, bindingAdapterPosition
                )
            }
            binding.optionsIb.setOnClickListener {
                optionsListener?.onClick(
                    it, bindingAdapterPosition
                )
            }

        }
    }


}