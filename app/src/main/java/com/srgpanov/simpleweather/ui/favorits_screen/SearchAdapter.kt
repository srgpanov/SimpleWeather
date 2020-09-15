package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.databinding.FavoriteSearchItemBinding
import com.srgpanov.simpleweather.databinding.SearchEmptyResultItemBinding

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    val featureMember: MutableList<FeatureMember> = mutableListOf()
    var listener: ((position: Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.favorite_search_item -> SearchHolder.SearchViewHolder(
                FavoriteSearchItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.search_empty_result_item -> SearchHolder.SearchEmptyViewHolder(
                SearchEmptyResultItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("wrong state")
        }
    }


    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        when (holder) {
            is SearchHolder.SearchViewHolder -> holder.bind(featureMember[position], listener)
            is SearchHolder.SearchEmptyViewHolder -> holder.bind()
        }

    }

    override fun getItemCount(): Int {
        return if (featureMember.isEmpty()) 1 else featureMember.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (featureMember.isEmpty())
            R.layout.search_empty_result_item
        else
            R.layout.favorite_search_item
    }

    fun setData(data: List<FeatureMember>) {
        featureMember.clear()
        featureMember.addAll(data)
        notifyDataSetChanged()
    }

    sealed class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class SearchViewHolder(val binding: FavoriteSearchItemBinding) :
            SearchHolder(binding.root) {
            fun bind(item: FeatureMember, listener: ((position: Int) -> Unit)?) {
                binding.placesTv.text = item.getFormattedName()
                binding.placesTv.setOnClickListener {
                    listener?.invoke(bindingAdapterPosition)
                }
            }
        }

        class SearchEmptyViewHolder(val binding: SearchEmptyResultItemBinding) :
            SearchHolder(binding.root) {
            fun bind() {
                binding.emptyDataTv.text = binding.root.context.getString(R.string.nothing_found)
            }
        }
    }

}