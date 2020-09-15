package com.srgpanov.simpleweather.ui.favorits_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.FavoritesViewItem
import com.srgpanov.simpleweather.other.getDrawableCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext


class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {
    val favorites: MutableList<FavoritesViewItem> = mutableListOf()
    var listener: ((itemBinding: FavoriteLocationItemBinding, position: Int) -> Unit)? =
        null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavoritesViewHolder(
            FavoriteLocationItemBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FavoritesViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        } else {
            val payLoads = payloads[0] as? List<FavoritesViewItem.Payload>
            if (payLoads != null) {
                holder.bindPayloads(payLoads)
            }

        }
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(favorites[position], listener)
    }


    override fun getItemCount(): Int {
        return favorites.size
    }


    suspend fun setData(newItems: List<FavoritesViewItem>) = withContext(Dispatchers.Default) {
        val oldItems = ArrayList(favorites)
        val placeDiffCallBack = PlaceDiffCallBack(oldItems, newItems)
        val resultDiff = DiffUtil.calculateDiff(placeDiffCallBack, false)
        if (isActive) {
            withContext(Dispatchers.Main) {
                favorites.clear()
                favorites.addAll(newItems)
                resultDiff.dispatchUpdatesTo(this@FavoritesAdapter)
            }
        }
    }


    class FavoritesViewHolder(val binding: FavoriteLocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            favorite: FavoritesViewItem,
            listener: ((itemBinding: FavoriteLocationItemBinding, position: Int) -> Unit)?
        ) {
            favorite.bind(binding)
            listener?.invoke(binding, bindingAdapterPosition)
        }

        fun bindPayloads(
            payLoads: List<FavoritesViewItem.Payload>
        ) {
            for (key in payLoads) {
                when (key) {
                    is FavoritesViewItem.Payload.Title -> binding.cityNameTv.text = key.new
                    is FavoritesViewItem.Payload.Background -> binding.constraintLayout.background =
                        binding.root.context.getDrawableCompat(key.new)
                    is FavoritesViewItem.Payload.Icon -> if (key.new != null) binding.cloudnessIv.setImageResource(
                        key.new
                    )
                    is FavoritesViewItem.Payload.Temp -> binding.tempValueTv.text = key.new
                    is FavoritesViewItem.Payload.CityTime -> binding.cityTimeTv.text = key.new
                }
            }
        }
    }
}