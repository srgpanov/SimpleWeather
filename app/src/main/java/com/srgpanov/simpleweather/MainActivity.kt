package com.srgpanov.simpleweather

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance())
                .commitNow()
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    }

    fun navigate(
        fragment: Class<out Fragment>,
        bundle: Bundle? = null,
        addToBackStack: Boolean = true
    ) {
        val instance = fragment.newInstance().apply {
            bundle?.let {
                this.arguments = it
            }
        }
        return when (addToBackStack) {
            true -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, instance, fragment.simpleName)
                    .addToBackStack(fragment.simpleName)
                    .commit()
                Unit
            }
            false -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, instance, fragment.simpleName)
                    .commit()
                Unit
            }
        }

        //todo Доделать навигацию
    }
}
