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

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

     var featureMember: MutableList<FeatureMember> = mutableListOf()
        get() = field
        private set(value) {
            field = value
        }
    var listener: MyClickListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchViewHolder(
            FavoriteSearchItemBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

            holder.bind(featureMember[position])

    }

    override fun getItemCount(): Int {
        return featureMember.size
    }


    fun setData(data: List<FeatureMember>) {
        featureMember.clear()
        featureMember.addAll(data)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(val binding: FavoriteSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeatureMember) {
            binding.placesTv.text = item.getFormatedName()
            binding.searchContainer.setOnClickListener {
                listener?.onClick(it, adapterPosition)
            }
        }
    }


}