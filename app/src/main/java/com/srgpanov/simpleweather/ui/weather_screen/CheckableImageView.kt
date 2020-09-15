package com.srgpanov.simpleweather.ui.weather_screen

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView


class CheckableImageView : AppCompatImageView, Checkable {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var mChecked = false

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState: IntArray = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) mergeDrawableStates(
            drawableState,
            CHECKED_STATE_SET
        )
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val onClickListener = OnClickListener { v ->
            toggle()
            l?.onClick(v)
        }
        super.setOnClickListener(onClickListener)
    }


    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}