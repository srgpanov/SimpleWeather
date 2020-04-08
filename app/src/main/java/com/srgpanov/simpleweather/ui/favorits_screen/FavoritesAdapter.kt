package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepository
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteCurrentItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSeparatorItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.formatTemp
import com.srgpanov.simpleweather.other.getWeatherIcon
import kotlinx.coroutines.*

class FavoritesAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var cuurent: PlaceEntity? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var places: MutableList<PlaceEntity> = mutableListOf()
    var listener: MyClickListener? = null
    var repository: DataRepository? = null

    companion object{
        const val CURRENT_POSITION=-2
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.favorite_current_item -> CurrentViewHolder(
                FavoriteCurrentItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.favorite_location_item -> FavoritesViewHolder(
                FavoriteLocationItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.favorite_separator_item->SeparatorViewHolder(
                FavoriteSeparatorItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentViewHolder -> holder.bind(
                cuurent ?: throw IllegalStateException("CurrentViewHolder bind")
            )
            is FavoritesViewHolder -> holder.bind(places[position - getOffset()])
        }

    }

    private fun getOffset(): Int {
        if (cuurent == null) {
            return 0
        } else
            return 2 //current and separator
    }

    override fun getItemCount(): Int {
        if (cuurent == null) {
            return places.size
        } else
            return places.size + getOffset()
    }

    override fun getItemViewType(position: Int): Int {
        return if (cuurent != null) {
            when (position) {
                0 -> R.layout.favorite_current_item
                1->R.layout.favorite_separator_item
                else -> R.layout.favorite_location_item
            }
        } else R.layout.favorite_location_item
    }

    fun setData(data: List<PlaceEntity>) {
        places.clear()
        places.addAll(data)
        notifyDataSetChanged()
    }


    inner class FavoritesViewHolder(private val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val scope = CoroutineScope(Dispatchers.IO + Job())
        fun bind(item: PlaceEntity) {
            binding.cityNameTv.text = item.cityTitle
            scope.launch {
                val weather = repository?.getWeather(item.geoPoint)
                weather?.let {
                    withContext(Dispatchers.Main) {
                        binding.cloudnessIv.setImageResource(getWeatherIcon(it.fact.icon))
                        binding.tempValueTv.text = formatTemp(it.fact.temp)
                        binding.cityTimeTv.text=it.getLocalTime()
                    }
                }
            }
            binding.constraintLayout.setOnClickListener {
                listener?.onClick(
                    it,adapterPosition-getOffset()
                )
            }

        }
    }

    inner class CurrentViewHolder(private val binding: FavoriteCurrentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val scope = CoroutineScope(Dispatchers.IO + Job())
        fun bind(item: PlaceEntity) {
            binding.cityNameTv.text = item.cityTitle
            scope.launch {
                val weather = repository?.getWeather(item.geoPoint)
                weather?.let {
                    withContext(Dispatchers.Main) {
                        binding.cloudnessIv.setImageResource(getWeatherIcon(it.fact.icon))
                        binding.tempValueTv.text = formatTemp(it.fact.temp)
                    }
                }
            }
            binding.constraintLayout.setOnClickListener {
                listener?.onClick(
                    it,CURRENT_POSITION
                )
            }
        }
    }
    inner class SeparatorViewHolder(private val binding: FavoriteSeparatorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}