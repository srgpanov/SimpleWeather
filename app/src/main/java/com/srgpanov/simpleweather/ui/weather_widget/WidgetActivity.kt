package com.srgpanov.simpleweather.ui.weather_widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.WidgetActivityLayoutBinding
import com.srgpanov.simpleweather.other.NavigationActivity
import com.srgpanov.simpleweather.other.addSystemWindowInsetToMargin
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.pager_screen.PagerFragment
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.OnLocationTypeChoiceCallback
import kotlin.properties.ReadOnlyProperty

class WidgetActivity : AppCompatActivity(), NavigationActivity {
    private lateinit var binding: WidgetActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WidgetActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
    override fun navigateToFragment(fragment: Fragment) {
        logD("navigateToFragment")
        val tag=fragment::class.java.simpleName
        supportFragmentManager.beginTransaction()
            .replace(R.id.widget_activity_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }




}


