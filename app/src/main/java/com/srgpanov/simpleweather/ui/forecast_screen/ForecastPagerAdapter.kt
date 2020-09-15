package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.databinding.ForecastPagerItemNewBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.Forecast
import com.srgpanov.simpleweather.other.FirstItemCompletelyVisibleListener
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding

class ForecastPagerAdapter : RecyclerView.Adapter<ForecastPagerAdapter.ForecastHolder>() {
    private val forecasts = mutableListOf<Forecast>()

    var itemVisibleListener: FirstItemCompletelyVisibleListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ForecastHolder(ForecastPagerItemNewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ForecastHolder, position: Int) {
        holder.bind(forecasts[position], itemVisibleListener)
    }

    override fun getItemCount(): Int {
        return forecasts.size
    }

    fun setData(list: List<Forecast>) {
        forecasts.clear()
        forecasts.addAll(list)
        notifyDataSetChanged()
    }

    class ForecastHolder(val binding: ForecastPagerItemNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val adapter: ForecastDayAdapter = ForecastDayAdapter()
        private val context = binding.root.context

        init {
            binding.recyclerView.adapter = adapter
            binding.recyclerView.addSystemWindowInsetToPadding(bottom = true)
            val decorator = CustomForecastItemDecoration(context)
            binding.recyclerView.addItemDecoration(decorator)
            binding.recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
            binding.recyclerView.layoutManager = ForecastLayoutManager(context)
        }

        fun bind(daily: Forecast, itemVisibleListener: FirstItemCompletelyVisibleListener?) {
            adapter.setData(daily)
            (binding.recyclerView.layoutManager as? ForecastLayoutManager)?.itemVisibleListener =
                itemVisibleListener
        }
    }
}