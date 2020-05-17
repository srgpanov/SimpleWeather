package com.srgpanov.simpleweather.ui.favorits_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.*


class FavoritesAdapter() : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    var places: MutableList<PlaceEntity> = mutableListOf()
        private set
    var listener: MyClickListener? = null
    var optionsListener: MyClickListener? = null
    var scope: CoroutineScope? = null
    var setDataJob: Job? = null

    companion object {
        const val CURRENT_POSITION = -2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavoritesViewHolder(
            FavoriteLocationItemBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

//    override fun onBindViewHolder(
//        holder: FavoritesViewHolder,
//        position: Int,
//        payloads: MutableList<Any>
//    ) {
//        if (payloads.isEmpty()) {
//            onBindViewHolder(holder,position)
//            return
//        } else {
//            val bundle = payloads[0] as Bundle
//            logD("onBindViewHolder ${bundle}")
//            for (key in bundle.keySet()) {
//                when (key) {
//                    TIME_STAMP -> holder.binding.cityTimeTv.text = bundle.getString(TIME_STAMP)
//                    TEMP -> holder.binding.tempValueTv.text = bundle.getString(TEMP)
//                    ICON -> holder.binding.cloudnessIv.setImageResource(bundle.getInt(ICON))
//                    TITLE -> holder.binding.cityNameTv.text = bundle.getString(TITLE)
//                }
//            }
//        }
//    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(places[position])
    }


    override fun getItemCount(): Int {
        return places.size
    }


    fun setData(data: List<PlaceEntity>) {
//        val oldItems = ArrayList(places)
//        val placeDiffCallBack = PlaceDiffCallBack(oldItems, data)
//        val resultDiff = DiffUtil.calculateDiff(placeDiffCallBack, false)
//        resultDiff.dispatchUpdatesTo(this@FavoritesAdapter)
//        places.clear()
//        places.addAll(data)
        setDataJob?.cancel()
        setDataJob = scope?.launch {
            data.forEach {
                logD("setData ${it.simpleWeather == null} ${it.simpleWeather?.currentWeatherResponse?.main?.temp}")
            }
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


    inner class FavoritesViewHolder(val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceEntity) {
            logD(item.toString())
            binding.cityNameTv.text = item.title
            val background = item.simpleWeather?.currentWeatherResponse?.weather?.get(0)
                ?.getWeatherBackground() ?: R.drawable.empty_weather_background
            binding.constraintLayout.background = binding.root.context.getDrawable(background)
            val response = item.simpleWeather?.currentWeatherResponse
            logD("$response")
            response?.let {
                binding.cloudnessIv.setImageResource(it.weather[0].getWeatherIcon())
                binding.tempValueTv.text = it.main.tempFormatted()
                binding.cityTimeTv.text =
                    item.simpleWeather?.currentWeatherResponse?.localTime()
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