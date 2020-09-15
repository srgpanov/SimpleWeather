package com.srgpanov.simpleweather.ui.weather_widget

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.WidgetActivityLayoutBinding
import com.srgpanov.simpleweather.other.NavigationActivity
import com.srgpanov.simpleweather.other.logD

class WidgetActivity : AppCompatActivity(), NavigationActivity {
    private lateinit var binding: WidgetActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WidgetActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.widget_activity_container, WidgetSetupFragment())
                .commit()
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    override fun navigateToFragment(fragment: Fragment) {
        logD("navigateToFragment")
        val tag = fragment::class.java.simpleName
        supportFragmentManager.beginTransaction()
            .replace(R.id.widget_activity_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }


}


