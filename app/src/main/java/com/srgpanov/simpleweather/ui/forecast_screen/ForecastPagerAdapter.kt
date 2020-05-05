package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.models.weather.Daily
import com.srgpanov.simpleweather.databinding.ForecastPagerItemNewBinding
import com.srgpanov.simpleweather.other.FirstItemCompletelyVisibleListener
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding
import com.srgpanov.simpleweather.other.logD

class ForecastPagerAdapter() : RecyclerView.Adapter<ForecastPagerAdapter.ForecastHolder>() {
    var forecasts = mutableListOf<Daily>()
        set(value) {
            field.clear()
            field.addAll(value)
            logD(value.toString())
            notifyDataSetChanged()
        }
    var clickListener: MyClickListener? = null
    var itemVisibleListener: FirstItemCompletelyVisibleListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ForecastHolder(ForecastPagerItemNewBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ForecastHolder, position: Int) {
        holder.bind(forecasts[position])
    }

    override fun getItemCount(): Int {
        return forecasts.size
    }

    inner class ForecastHolder(binding: ForecastPagerItemNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var adapter: ForecastDayAdapter = ForecastDayAdapter()
        private val context = binding.root.context

        init {
            binding.recyclerView.adapter = adapter
            binding.recyclerView.addSystemWindowInsetToPadding(bottom = true)
            val decorator = CustomForecastItemDecoration(context)
            binding.recyclerView.addItemDecoration(decorator)
            binding.recyclerView.overScrollMode= View.OVER_SCROLL_NEVER
            binding.recyclerView.layoutManager = object : LinearLayoutManager(context) {
                override fun onLayoutCompleted(state: RecyclerView.State?) {
                    super.onLayoutCompleted(state)
                    // triggered when there's a re-layout (item added/removed etc)
                    if (findFirstCompletelyVisibleItemPosition() == 0)
                        itemVisibleListener?.isVisible(true)
                    else
                        itemVisibleListener?.isVisible(false)
                }

                override fun offsetChildrenVertical(dy: Int) {
                    super.offsetChildrenVertical(dy)
                    // triggered during scroll
                    if (findFirstCompletelyVisibleItemPosition() == 0)
                        itemVisibleListener?.isVisible(true)
                    else
                        itemVisibleListener?.isVisible(false)
                }
            }
        }

        fun bind(daily: Daily) {
            adapter.setData(daily)
        }
    }
}