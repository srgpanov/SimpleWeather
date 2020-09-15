package com.srgpanov.simpleweather.ui.favorits_screen

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.EmptyFavoriteItemBinding

class EmptyFavoriteAdapter : RecyclerView.Adapter<EmptyFavoriteAdapter.EmptyItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyItemsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EmptyItemsViewHolder(
            EmptyFavoriteItemBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EmptyItemsViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 1
    }

    class EmptyItemsViewHolder(binding: EmptyFavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val context = binding.root.context
            val unicode = 0x2B50
            val starEmodji = String(Character.toChars(unicode))
            val sb =
                SpannableStringBuilder().append(context.getString(R.string.empty_vaforite_text_1))
                    .bold { append(context.getString(R.string.empty_vaforite_text_2)) }
                    .append(
                        context.getString(R.string.empty_vaforite_text_3)
                                + "\n\n"
                                + context.getString(R.string.empty_vaforite_text_4) + starEmodji
                    )
            binding.emptyFavoriteTv.text = sb
        }
    }
}