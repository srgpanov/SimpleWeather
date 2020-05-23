package com.srgpanov.simpleweather.ui.setting_widget_screen

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.SettingWidgetFragmentBinding
import com.srgpanov.simpleweather.other.OnBackPressedListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.pager_screen.PagerAdapter
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget


class SettingWidgetFragment:Fragment() {
    private var _binding: SettingWidgetFragmentBinding? = null
    private val binding get() = _binding!!
    private var mainActivity: MainActivity?=null
    private var actionBar: ActionBar? = null
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
        actionBar=null
        mainActivity?.setSupportActionBar(null)
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            parentFragmentManager.popBackStack()
        }
        return super.onOptionsItemSelected(item)
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
        val statusView = binding.statusBarView
        ViewCompat.setOnApplyWindowInsetsListener(statusView) { view, insets ->
            view.updateLayoutParams {
                if (insets.systemWindowInsetTop != 0) {
                    height = insets.systemWindowInsetTop
                }
            }
            insets
        }
        statusView.requestApplyInsetsWhenAttached()
    }

    private fun setupToolbar() {
        mainActivity?.setSupportActionBar(binding.toolbar)
        actionBar = mainActivity?.getSupportActionBar()
        actionBar?.setTitle(getString(R.string.widget_settings))
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        setHasOptionsMenu(true)
    }


}