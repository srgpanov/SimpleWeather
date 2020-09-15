package com.srgpanov.simpleweather.ui.setting_widget_screen

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.SettingWidgetFragmentBinding
import com.srgpanov.simpleweather.other.InsetSide
import com.srgpanov.simpleweather.other.setHeightOrWidthAsSystemWindowInset
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget


class SettingWidgetFragment:Fragment() {
    private var _binding: SettingWidgetFragmentBinding? = null
    private val binding get() = _binding!!
    private var mainActivity: MainActivity?=null
    private lateinit var widgetManager: AppWidgetManager
    private lateinit var pagerAdapter:SettingWidgetAdapter

    companion object {
        val TAG = this::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingWidgetFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as? MainActivity
        setupInsets()
        setupToolbar()
        setupViewPager()
    }

    override fun onDestroyView() {
        binding.viewPager.adapter=null
        _binding=null
        super.onDestroyView()
    }


    private fun setupViewPager() {
        widgetManager = AppWidgetManager.getInstance(requireActivity())
        val widgetComponent = ComponentName(requireActivity(), WeatherWidget::class.java)
        val appWidgetIds = widgetManager.getAppWidgetIds(widgetComponent)
        pagerAdapter= SettingWidgetAdapter(this)
        pagerAdapter.widgetIds=appWidgetIds.toMutableList()
        binding.viewPager.adapter=pagerAdapter

    }
    private fun setupInsets() {
        binding.statusBarView.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.title = getString(R.string.widget_settings)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}