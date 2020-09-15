package com.srgpanov.simpleweather.ui.forecast_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.databinding.CalendarDayItemBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.forecast.CalendarItem

class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {
    private var dates: MutableList<CalendarItem> = mutableListOf()
    var listener: ((position: Int) -> Unit)? = null
    private var recyclerView: RecyclerView? = null

    companion object {
        private const val DAYS_IN_WEEK = 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = DateViewHolder(CalendarDayItemBinding.inflate(inflater, parent, false))
        holder.itemView.layoutParams.width = parent.width / DAYS_IN_WEEK
        return holder
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], listener)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    fun setData(data: List<CalendarItem>) {
        dates.clear()
        dates.addAll(data)
        notifyDataSetChanged()
    }

    fun selectDay(position: Int) {
        if (position >= dates.size) return
        var wasSelected = 0
        for ((index, item) in dates.withIndex()) {
            if (item.isSelected) {
                if (index == position) return
                wasSelected = index
                dates[wasSelected] = item.copy(isSelected = false)
                break
            }
        }
        dates[position] = dates[position].copy(isSelected = true)
        if (position >= itemCount - 2)
            recyclerView?.scrollToPosition(dates.lastIndex)
        if (position <= 1)
            recyclerView?.scrollToPosition(0)
        notifyItemChanged(wasSelected)
        notifyItemChanged(position)
    }

    class DateViewHolder(val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarItem, listener: ((position: Int) -> Unit)?) {
            item.bind(binding)
            binding.linearLayout.setOnClickListener {
                listener?.invoke(bindingAdapterPosition)
            }
        }
    }
}