package com.srgpanov.simpleweather.ui.setting_widget_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidgetSettingListFragment
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidgetSettingListFragment.Companion.WIDGET_ID_KEY

class SettingWidgetAdapter(fragment: Fragment):FragmentStateAdapter(fragment) {
    var widgetIds:MutableList<Int> = mutableListOf()
    override fun getItemCount(): Int {
        return widgetIds.size
    }

    override fun createFragment(position: Int): Fragment {
        widgetIds.forEach {
            logD("setupViewPager $it")
        }
        return WeatherWidgetSettingListFragment().apply {
            arguments = Bundle().apply { putInt(WIDGET_ID_KEY, widgetIds[position]) }
        }
    }

}