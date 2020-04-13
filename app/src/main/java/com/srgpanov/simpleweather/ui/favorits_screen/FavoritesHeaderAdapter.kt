package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteCurrentItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSeparatorItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.formatTemp
import com.srgpanov.simpleweather.other.getWeatherIcon
import kotlinx.coroutines.*

class FavoritesHeaderAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var current: PlaceEntity? = null
    var listener: MyClickListener? = null
    var repository: DataRepository? = null
     var scope: CoroutineScope?=null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            R.layout.favorite_current_item -> CurrentViewHolder(
                FavoriteCurrentItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.favorite_separator_item -> SeparatorViewHolder(
                FavoriteSeparatorItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int {
        if(current==null){
            return 0
        }else return 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position){
            0-> R.layout.favorite_current_item
            1->R.layout.favorite_separator_item
            else->throw  IllegalStateException("wrong state")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentViewHolder -> holder.bind(
                current ?: throw IllegalStateException("CurrentViewHolder bind")
            )
        }
    }

    inner class CurrentViewHolder(private val binding: FavoriteCurrentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceEntity) {
            binding.cityNameTv.text = item.cityTitle
            scope?.launch {
                val weather = repository?.getWeather(item.toGeoPoint())
                weather?.let {
                    withContext(Dispatchers.Main) {
                        binding.cloudnessIv.setImageResource(getWeatherIcon(it.fact.icon))
                        binding.tempValueTv.text = formatTemp(it.fact.temp)
                    }
                }
            }
            binding.constraintLayout.setOnClickListener {
                listener?.onClick(
                    it, FavoritesAdapter.CURRENT_POSITION
                )
            }
        }
    }
    inner class SeparatorViewHolder(private val binding: FavoriteSeparatorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}
