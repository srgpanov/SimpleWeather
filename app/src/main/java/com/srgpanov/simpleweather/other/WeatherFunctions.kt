package com.srgpanov.simpleweather.other

import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

fun Context.getDrawableCompat(@DrawableRes res: Int): Drawable? {
    return ContextCompat.getDrawable(this, res)
}

fun Context.getColorCompat(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}

fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}

fun <T> Flow<T>.collectIn(scope: CoroutineScope, block: (T) -> Unit): Job = scope.launch {
    collect { param: T ->
        block.invoke(param)
    } // tail-call
}

fun View.showSnackBar(
    @StringRes stringRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    animationMode: Int = ANIMATION_MODE_SLIDE
) {
    Snackbar.make(this, stringRes, duration).apply { this.animationMode = animationMode }.show()
}

inline fun <reified T> ValueAnimator.animatedValue() = animatedValue as T

fun <T> Iterable<T>.replace(replacementItem: T, predicate: (T) -> Boolean) =
    map { if (predicate(it)) replacementItem else it }

@FlowPreview
fun <T> Flow<Iterable<T>>.fromIterable(): Flow<T> = flow {
    this@fromIterable.onEach { list ->
        for (item in list) {
            emit(item)
        }
    }
}

fun Date.format(
    pattern: String = "dd.MM.yyyy HH:mm:ss",
    locale: Locale = Locale.getDefault()
): String {
    return SimpleDateFormat(pattern, locale).format(this)
}
