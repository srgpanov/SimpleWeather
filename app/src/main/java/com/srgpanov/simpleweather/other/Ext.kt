package com.srgpanov.simpleweather.other

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.R
import java.math.RoundingMode
import java.util.*
import kotlin.math.abs
import kotlin.reflect.KClass


inline fun <reified T> T.logD(message: String = "TAG $this") =
    Log.d(getClassSimpleName(T::class), message)

inline fun <reified T> T.logE(message: String = "TAG $this") =
    Log.e(getClassSimpleName(T::class), message)

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
            Log.e("TAG  ", "Cant find image")
            R.drawable.ic_ovc
        }
    }
}

fun weatherIsFresh(timeStamp: Long): Boolean {
    val b = abs(System.currentTimeMillis() - timeStamp) < REFRESH_TIME
    Log.d("weatherIsFresh", "timestamp:  ${Date(timeStamp).format()} ")
    Log.d(
        "weatherIsFresh",
        "time from last response " +
                "${Date(abs(System.currentTimeMillis() - timeStamp)).format()}  isFresh=${b}"
    )
    return b
}

fun weatherIsActual(timeStamp: Long): Boolean {
    val b = abs(System.currentTimeMillis() - timeStamp) < WEATHER_IS_ACTUAL
    Log.d("weatherIsNotActual", "timestamp:  ${Date(timeStamp).format()} ")
    Log.d(
        "weatherIsNotActual",
        "time from last response " +
                "${Date(abs(System.currentTimeMillis() - timeStamp)).format()}  IsNotActual=${b}"
    )
    return b
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

fun View.setHeightOrWidthAsSystemWindowInset(
    side: InsetSide,
    onApplyInsets: ((inset: Int) -> Unit)? = null
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            when (side) {
                InsetSide.LEFT -> {
                    width = insets.systemWindowInsetLeft
                    onApplyInsets?.invoke(insets.systemWindowInsetLeft)
                }
                InsetSide.RIGHT -> {
                    width = insets.systemWindowInsetRight
                    onApplyInsets?.invoke(insets.systemWindowInsetRight)
                }
                InsetSide.BOTTOM -> {
                    height = insets.systemWindowInsetBottom
                    onApplyInsets?.invoke(insets.systemWindowInsetBottom)
                }
                InsetSide.TOP -> {
                    height = insets.systemWindowInsetTop
                    onApplyInsets?.invoke(insets.systemWindowInsetTop)
                }
            }
        }
        insets
    }
    requestApplyInsetsWhenAttached()
}

enum class InsetSide {
    LEFT, RIGHT, BOTTOM, TOP
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

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun Double.numbersAfterDot(numbers: Int = 2): Double {
    return this.toBigDecimal().setScale(numbers, RoundingMode.DOWN).toDouble()
}

val Fragment.TAG: String
    get() = this::class.java.simpleName


inline fun <reified T> T.getClassSimpleName(enclosingClass: KClass<*>?): String =

    if (T::class.java.simpleName.isNotBlank()) {
        T::class.java.simpleName
    } else { // Enforce the caller to pass a class to retrieve its simple name
        enclosingClass?.simpleName ?: "Anonymous"
    }