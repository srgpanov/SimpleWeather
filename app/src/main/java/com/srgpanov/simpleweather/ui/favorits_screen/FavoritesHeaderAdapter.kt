package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.FavoriteCurrentItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteSeparatorItemBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.CurrentViewItem

class FavoritesHeaderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var current: CurrentViewItem? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var listener: ((itemBinding: FavoriteCurrentItemBinding, item: CurrentViewItem?) -> Unit)? =
        null
    private val separator = 1
    private val dataItem = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.favorite_current_item -> CurrentViewHolder(
                FavoriteCurrentItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.favorite_separator_item -> SeparatorViewHolder(
                FavoriteSeparatorItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int {
        return dataItem + separator
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.favorite_current_item
            1 -> R.layout.favorite_separator_item
            else -> throw  IllegalStateException("wrong state")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentViewHolder -> holder.bind(current)
        }
    }

    inner class CurrentViewHolder(private val binding: FavoriteCurrentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewItem: CurrentViewItem?) {
            val current = viewItem ?: CurrentViewItem.emptyCurrent(binding.root.context)
            current.bind(binding)
            listener?.invoke(binding, viewItem)
        }
    }

    class SeparatorViewHolder(binding: FavoriteSeparatorItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
