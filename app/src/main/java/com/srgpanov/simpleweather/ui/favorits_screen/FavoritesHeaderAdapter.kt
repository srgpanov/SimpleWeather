package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteCurrentItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSeparatorItemBinding
import com.srgpanov.simpleweather.other.MyClickListener

class FavoritesHeaderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var current: PlaceEntity? = null
    var listener: MyClickListener? = null
    val SEPARATOR =1
    val DATA_ITEM =1


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
         return DATA_ITEM+SEPARATOR
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.favorite_current_item
            1 -> R.layout.favorite_separator_item
            else -> throw  IllegalStateException("wrong state")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentViewHolder -> holder.bind(current)
        }
    }

    inner class CurrentViewHolder(private val binding: FavoriteCurrentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceEntity?) {
            if (item!=null){
            binding.cityNameTv.text = item.cityTitle
            val weather = item.oneCallResponse
            if (weather!=null){
                val weatherIcon =
                    weather.current.weather[0].getWeatherIcon()
                binding.cloudnessIv.setImageResource(weatherIcon)
                binding.tempValueTv.text = weather.current.tempFormatted()
            }else{
                binding.tempValueTv.text="—"
            }

            binding.constraintLayout.setOnClickListener {
                listener?.onClick(
                    it, FavoritesAdapter.CURRENT_POSITION
                )
            }}else{
                binding.cityNameTv.text= binding.root.context.getString(R.string.current_location)
                binding.tempValueTv.text="—"
            }
        }
    }

    inner class SeparatorViewHolder(private val binding: FavoriteSeparatorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}
