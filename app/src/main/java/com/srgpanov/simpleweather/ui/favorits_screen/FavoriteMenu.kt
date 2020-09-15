package com.srgpanov.simpleweather.ui.favorits_screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.other.dpToPx

class FavoriteMenu(private val anchorView: View) : PopupWindow(anchorView.context) {
    private val context = anchorView.context
    private val inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("InflateParams")
    private val layout: View = inflater.inflate(R.layout.favorite_popup_menu_layout, null)
    private val removeView = layout.findViewById<View>(R.id.menu_remove_tv)
    private val renameView = layout.findViewById<View>(R.id.menu_rename_tv)

    var onRemoveClick: (() -> Unit)? = null
    var onRenameClick: (() -> Unit)? = null

    init {
        contentView = layout
        val menuHeight = dpToPx(96)
        height = menuHeight
        isFocusable = true
        elevation = 8F
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        removeView.setOnClickListener {
            this.dismiss()
            onRemoveClick?.invoke()
        }
        renameView.setOnClickListener {
            this.dismiss()
            onRenameClick?.invoke()
        }
    }

    fun show(display: Display) {
        animationStyle = if (menuPinnedTop(display)) {
            R.style.AnimationPopupTop
        } else {
            R.style.AnimationPopupBottom
        }
        showAsDropDown(anchorView)
    }

    private fun menuPinnedTop(
        display: Display
    ): Boolean {
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val size = Point()
        display.getSize(size)
        val displayHeight: Int = size.y
        return (location[1] + anchorView.height + height) > displayHeight
    }
}