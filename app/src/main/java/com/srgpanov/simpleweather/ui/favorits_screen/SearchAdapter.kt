package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.databinding.FavoriteSearchHeaderItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSearchItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD
import java.lang.IllegalStateException

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var featureMember: MutableList<FeatureMember> = mutableListOf()
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
            R.layout.favorite_search_header_item->SearchHeaderVIewHolder(
                FavoriteSearchHeaderItemBinding.inflate(
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
            holder.bind(featureMember[position - HEADER])
        }
    }

    override fun getItemCount(): Int {
        return featureMember.size + HEADER
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.favorite_search_header_item
        } else
            R.layout.favorite_search_item
    }

    fun setData(data: List<FeatureMember>) {
        featureMember.clear()
        featureMember.addAll(data)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(val binding: FavoriteSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeatureMember) {
            binding.placesTv.text = item.GeoObject.name
            logD("${item.GeoObject.description} ${item.GeoObject.metaDataProperty.GeocoderMetaData.Address}")
            binding.searchContainer.setOnClickListener {
                listener?.onClick(it, adapterPosition + HEADER)
            }
        }
    }

    inner class SearchHeaderVIewHolder(val binding: FavoriteSearchHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}