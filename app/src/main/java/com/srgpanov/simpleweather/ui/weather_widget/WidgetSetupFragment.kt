package com.srgpanov.simpleweather.ui.weather_widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.databinding.WidgetSetupFragmentLayoutBinding
import com.srgpanov.simpleweather.other.addSystemWindowInsetToMargin
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached

class WidgetSetupFragment : Fragment() {
    private var _binding: WidgetSetupFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    lateinit var resultValue: Intent
    var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID

    companion object {
        val TAG = WeatherWidgetSettingListFragment::class.java.simpleName
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WidgetSetupFragmentLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        setupWidget()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun setupInsets() {
        binding.createWidgetButton.addSystemWindowInsetToMargin(bottom = true)
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
        binding.createWidgetButton.doOnPreDraw {
            val listFragment =
                childFragmentManager.findFragmentByTag(WeatherWidgetSettingListFragment.TAG) as? WeatherWidgetSettingListFragment
            listFragment?.addButtonHeightToPadding(it.height)
        }
    }

    private fun setupWidget() {
        val intent = requireActivity().intent
        logD("setupWidget $intent")
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            );
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            logD("setupWidget widgetID == AppWidgetManager.INVALID_APPWIDGET_ID")
            requireActivity().finish();
        }
        resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        requireActivity().setResult(Activity.RESULT_CANCELED, resultValue)
    }

    private fun setupListeners() {
        binding.createWidgetButton.setOnClickListener {
            onCreateWidgetClick()
        }
    }


    private fun onCreateWidgetClick() {
        requireActivity().setResult(Activity.RESULT_OK, resultValue)
        WeatherWidget.updateWidget(  widgetID)
        logD("finish config $widgetID")
        requireActivity().finish()
    }
}