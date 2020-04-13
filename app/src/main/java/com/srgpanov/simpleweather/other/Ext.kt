package com.srgpanov.simpleweather.other

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import com.srgpanov.simpleweather.R

fun Any.logD(message: String= "TAG $this") {
    Log.d(this.className, message)
}
fun logDAnonim(message: String= "") {
    Log.d("TAG", message)
}
fun Any.logE(message: String= "TAG $this") {
    Log.e(this.className, message)
}
private val Any.className: String
    get() = (this::class.java.simpleName)

fun formatTemp(temp: Int): String {
    if (temp > 0) {
        return "+${temp}°"
    } else {
        if (temp < 0) {
            return "${temp}°"
        }
        return "${temp}°"
    }

}
fun getWeatherIcon(weather: String): Int {
    return when (weather) {
        "bkn_-ra_d" -> R.drawable.ic_bkn__ra_d
        "bkn_-ra_n" -> R.drawable.ic_bkn__ra_n
        "bkn_-sn_d" -> R.drawable.ic_bkn__sn_d
        "bkn_-sn_n" -> R.drawable.ic_bkn__sn_n
        "bkn_d" -> R.drawable.ic_bkn_d
        "bkn_n" -> R.drawable.ic_bkn_n
        "bkn_ra_d" -> R.drawable.ic_bkn_ra_d
        "bkn_ra_n" -> R.drawable.ic_bkn_ra_n
        "bkn_sn_d" -> R.drawable.ic_bkn_sn_d
        "bkn_sn_n" -> R.drawable.ic_bkn_sn_n
        "bl" -> R.drawable.ic_bl
        "fg_d" -> R.drawable.ic_fg_d
        "ovc" -> R.drawable.ic_ovc
        "ovc_-ra" -> R.drawable.ic_ovc__ra
        "ovc_-sn" -> R.drawable.ic_ovc__sn
        "ovc_ra" -> R.drawable.ic_ovc_ra
        "ovc_sn" -> R.drawable.ic_ovc_sn
        "ovc_ts_ra" -> R.drawable.ic_ovc_ts_ra
        "skc_d" -> R.drawable.ic_skc_d
        "skc_n" -> R.drawable.ic_skc_n
        else -> {
            Log.e("TAG  ","Cant find image")
            R.drawable.ic_ovc
        }
    }
}
fun getWindDirectionIcon(direction:String):Int{
    return when(direction){
        "nw"->R.drawable.ic_se
        "n" ->R.drawable.ic_south
        "ne"->R.drawable.ic_ne
        "e" ->R.drawable.ic_east
        "se"->R.drawable.ic_nw
        "s" ->R.drawable.ic_north
        "sw"->R.drawable.ic_sw
        "w" ->R.drawable.ic_west
        "с"->R.drawable.ic_calm
        else -> throw IllegalStateException("Wind icon")
    }
}
fun View.addSystemWindowInsetToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(paddingLeft, paddingTop, paddingRight, paddingBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updatePadding(
            left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
            top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
            right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
            bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
        )

        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) {
    val (initialLeft, initialTop, initialRight, initialBottom) =
        listOf(marginLeft, marginTop, marginRight, marginBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            (this as? ViewGroup.MarginLayoutParams)?.let {
                updateMargins(
                    left = initialLeft + (if (left) insets.systemWindowInsetLeft else 0),
                    top = initialTop + (if (top) insets.systemWindowInsetTop else 0),
                    right = initialRight + (if (right) insets.systemWindowInsetRight else 0),
                    bottom = initialBottom + (if (bottom) insets.systemWindowInsetBottom else 0)
                )
            }
        }

        insets
    }
    requestApplyInsetsWhenAttached()
}
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
fun dpToPx(dp:Int):Int{
    return (dp*Resources.getSystem().displayMetrics.density).toInt()
}
fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}