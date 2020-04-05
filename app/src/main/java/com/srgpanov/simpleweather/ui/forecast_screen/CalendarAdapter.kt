package com.srgpanov.simpleweather.ui.forecast_screen

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.data.entity.CalendarItem
import com.srgpanov.simpleweather.databinding.CalendarDayItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter() : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {
    private var  dates :MutableList<CalendarItem> = mutableListOf()
    var listener: MyClickListener? = null
    private val DAYS_IN_WEEK=7

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = DateViewHolder(CalendarDayItemBinding.inflate(inflater, parent, false))
        holder.itemView.layoutParams.width=parent.width/DAYS_IN_WEEK
        return holder
    }



    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount(): Int {
        return dates.size
    }
    fun setData(data:List<CalendarItem>){
        dates.clear()
        dates.addAll(data)
        notifyDataSetChanged()
    }
    fun selectDay(position: Int){
        var wasSelected=0
        dates.forEachIndexed() {index,item->
            if(item.isSelected) {
                wasSelected=index
                item.isSelected=false
            }
        }
        dates[position].isSelected=true
        notifyItemChanged(wasSelected)
        notifyItemChanged(position)
    }

    inner class DateViewHolder(val binding: CalendarDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarItem){
            binding.day.text=SimpleDateFormat("E",Locale.ENGLISH).format(item.date)
            binding.numbers.text=item.date.date.toString()
            binding.linearLayout.setOnClickListener { view: View? ->
                listener?.onClick(view,adapterPosition)
            }
            if (item.isSelected){
                binding.circle.visibility=View.VISIBLE
                binding.numbers.setTextColor(Color.WHITE)
            }else{
                binding.circle.visibility=View.INVISIBLE
                binding.numbers.setTextColor(Color.parseColor("#212121"))
            }
        }
    }
}