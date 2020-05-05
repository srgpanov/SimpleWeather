package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FavoriteSearchHeaderItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSearchItemBinding
import com.srgpanov.simpleweather.databinding.SearchHistoryEmptyItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import java.lang.IllegalStateException

class SearchHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var searchHistoryList: MutableList<PlaceEntity> = mutableListOf()
        get() = field
        private set(value) {
            field = value
        }
    var listener: MyClickListener? = null

    companion object {
        const val HEADER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType){
            R.layout.favorite_search_item->SearchViewHolder(
                FavoriteSearchItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.favorite_search_header_item->SearchHeaderViewHolder(
                FavoriteSearchHeaderItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.search_history_empty_item->SearchEmptyViewHolder(
                SearchHistoryEmptyItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("wrong state")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SearchViewHolder){
            holder.bind(searchHistoryList[position - HEADER])
        }
    }

    override fun getItemCount(): Int {
        if (searchHistoryList.isEmpty()){
            return 1
        }else{
            return searchHistoryList.size + HEADER
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (searchHistoryList.isEmpty()){
            return R.layout.search_history_empty_item
        }else{
            return if (position == 0) {
                R.layout.favorite_search_header_item
            } else
                R.layout.favorite_search_item
        }

    }

    fun setData(data: List<PlaceEntity>) {
        searchHistoryList.clear()
        searchHistoryList.addAll(data)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(val binding: FavoriteSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaceEntity) {
            binding.placesTv.text = item.cityFullName
            binding.searchContainer.setOnClickListener {
                //todo
                listener?.onClick(it, bindingAdapterPosition - HEADER)
            }
        }
    }

    inner class SearchHeaderViewHolder(val binding: FavoriteSearchHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
    inner class SearchEmptyViewHolder(binding: SearchHistoryEmptyItemBinding):
            RecyclerView.ViewHolder(binding.root)
}