package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.entity.CalendarItem
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.other.MyClickListener

class FavoritesAdapter(): RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    private var  requests :MutableList<WeatherResponse> = mutableListOf()
    var listener: MyClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return FavoritesViewHolder(FavoriteLocationItemBinding.inflate(inflater, parent, false))
    }




    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return requests.size
    }
    fun setData(data:List<WeatherResponse>){
        requests.clear()
        requests.addAll(data)
        notifyDataSetChanged()
    }


    inner class FavoritesViewHolder(val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarItem){

        }
    }


}