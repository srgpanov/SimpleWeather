package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job


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


    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(places[position])
    }


    override fun getItemCount(): Int {
        return places.size
    }


    fun setData(data: List<PlaceEntity>) {
        val oldItems = ArrayList(places)
        val placeDiffCallBack = PlaceDiffCallBack(oldItems, data)
        val resultDiff = DiffUtil.calculateDiff(placeDiffCallBack, false)
        resultDiff.dispatchUpdatesTo(this@FavoritesAdapter)
        places.clear()
        places.addAll(data)
//        setDataJob?.cancel()
//        setDataJob = scope?.launch {
//            data.forEach {
//                logD("setData ${it.simpleWeatherTable==null} ${it.simpleWeatherTable?.currentWeatherResponse?.main?.temp}")
//            }
//            val oldItems = ArrayList(places)
//            val placeDiffCallBack = PlaceDiffCallBack(oldItems, data)
//            val resultDiff = DiffUtil.calculateDiff(placeDiffCallBack, false)
//            withContext(Dispatchers.Main) {
//                resultDiff.dispatchUpdatesTo(this@FavoritesAdapter)
//                places.clear()
//                places.addAll(data)
//            }
//        }
    }


    inner class FavoritesViewHolder(private val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceEntity) {
            logD(item.toString())
            binding.cityNameTv.text = item.cityTitle
            val background = item.simpleWeatherTable?.currentWeatherResponse?.weather?.get(0)
                ?.getWeatherBackground() ?: R.drawable.empty_weather_background
            binding.constraintLayout.background = binding.root.context.getDrawable(background)
            val response = item.simpleWeatherTable?.currentWeatherResponse
            logD("$response")
            response?.let {
                binding.cloudnessIv.setImageResource(it.weather[0].getWeatherIcon())
                binding.tempValueTv.text = it.main.tempFormatted()
                binding.cityTimeTv.text =
                    item.simpleWeatherTable?.currentWeatherResponse?.localTime()
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