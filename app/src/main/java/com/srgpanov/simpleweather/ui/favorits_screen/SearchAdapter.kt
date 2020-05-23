package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.places.FeatureMember
import com.srgpanov.simpleweather.databinding.FavoriteSearchItemBinding
import com.srgpanov.simpleweather.databinding.SearchEmptyResultItemBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.logD

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    var featureMember: MutableList<FeatureMember> = mutableListOf()
        get() = field
        private set(value) {
            field = value
        }
    var listener: MyClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val inflater = LayoutInflater.from(parent.context)
        logD("empty viewType $viewType")
        return when (viewType){
            R.layout.favorite_search_item->SearchHolder.SearchViewHolder(
                FavoriteSearchItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.search_empty_result_item->SearchHolder.SearchEmptyViewHolder(
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
        when(holder){
            is SearchHolder.SearchViewHolder -> holder.bind(featureMember[position],listener)
            is SearchHolder.SearchEmptyViewHolder ->holder.bind()
        }

    }
    override fun getItemCount(): Int {
        if (featureMember.isEmpty()){
            return 1
        }else{
            return featureMember.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (featureMember.isNotEmpty()){
            logD("empty false")
            return R.layout.favorite_search_item
        }else{
            logD("empty true")
            return R.layout.search_empty_result_item
        }
    }

    fun setData(data: List<FeatureMember>) {
        featureMember.clear()
        featureMember.addAll(data)
        notifyDataSetChanged()
    }

    sealed class SearchHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    class SearchViewHolder(val binding: FavoriteSearchItemBinding) :
        SearchHolder(binding.root) {
        fun bind(item: FeatureMember,listener:MyClickListener?) {
            binding.placesTv.text = item.getFormattedName()
            binding.placesTv.setOnClickListener {
                listener?.onClick(it, bindingAdapterPosition)
            }
        }
    }
    class SearchEmptyViewHolder(val binding: SearchEmptyResultItemBinding):
        SearchHolder(binding.root){
        fun bind() {
            logD("empty")
            binding.emptyDataTv.text=binding.root.context.getString(R.string.nothing_found)
        }
    }}

}