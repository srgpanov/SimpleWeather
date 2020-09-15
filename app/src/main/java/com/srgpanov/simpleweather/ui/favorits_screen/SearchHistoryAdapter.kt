package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.FavoriteSearchHeaderItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSearchItemBinding
import com.srgpanov.simpleweather.databinding.SearchHistoryEmptyItemBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem

class SearchHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var searchHistoryList: MutableList<PlaceViewItem> = mutableListOf()
        private set
    var listener: ((position: Int) -> Unit)? = null

    companion object {
        const val HEADER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.favorite_search_item -> SearchViewHolder(
                FavoriteSearchItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.favorite_search_header_item -> SearchHeaderViewHolder(
                FavoriteSearchHeaderItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.search_history_empty_item -> SearchEmptyViewHolder(
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
        if (holder is SearchViewHolder) {
            holder.bind(searchHistoryList[position - HEADER], listener)
        }
    }

    override fun getItemCount(): Int {
        return if (searchHistoryList.isEmpty()) {
            1
        } else {
            searchHistoryList.size + HEADER
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (searchHistoryList.isEmpty()) {
            R.layout.search_history_empty_item
        } else {
            if (position == 0)
                R.layout.favorite_search_header_item
            else
                R.layout.favorite_search_item
        }

    }

    fun setData(data: List<PlaceViewItem>) {
        searchHistoryList.clear()
        searchHistoryList.addAll(data)
        notifyDataSetChanged()
    }

    class SearchViewHolder(val binding: FavoriteSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewItem: PlaceViewItem, listener: ((position: Int) -> Unit)?) {
            binding.placesTv.text = viewItem.cityFullName
            binding.placesTv.setOnClickListener {
                listener?.invoke(bindingAdapterPosition - HEADER)
            }
        }
    }

    class SearchHeaderViewHolder(val binding: FavoriteSearchHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class SearchEmptyViewHolder(binding: SearchHistoryEmptyItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}