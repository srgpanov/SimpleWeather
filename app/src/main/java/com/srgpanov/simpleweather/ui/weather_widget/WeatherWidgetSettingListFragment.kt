package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.switchmaterial.SwitchMaterial
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.formatTemp
import com.srgpanov.simpleweather.databinding.WidgetSettingsFagmentLayoutBinding
import com.srgpanov.simpleweather.di.ArgumentsViewModelFactory
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment.Companion.KEY_REQUEST_PLACE
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CERTAIN
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.OnLocationTypeChoiceCallback
import com.srgpanov.simpleweather.ui.weather_widget.SettingWidgetViewModel.Companion.ALPHA_MAX_VALUE
import com.srgpanov.simpleweather.ui.weather_widget.SettingWidgetViewModel.Companion.ARGUMENT_WIDGET
import java.util.*
import javax.inject.Inject

class WeatherWidgetSettingListFragment : Fragment(), FragmentResultListener {
    private var _binding: WidgetSettingsFagmentLayoutBinding? = null
    private val binding get() = _binding!!
    private var widgetID = INVALID_APPWIDGET_ID

    @Inject
    internal lateinit var settingsViewModelFactory: SettingWidgetViewModel.SettingsListViewModelFactory
    lateinit var viewModel: SettingWidgetViewModel


    companion object {
        val TAG = WeatherWidgetSettingListFragment::class.java.simpleName
        const val WIDGET_ID_KEY = "WIDGET_ID_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectWidgetSettingsListFragment(this)
        setupWidgetId()
        val factory: ArgumentsViewModelFactory<SettingWidgetViewModel> = ArgumentsViewModelFactory(
            settingsViewModelFactory,
            bundleOf(ARGUMENT_WIDGET to widgetID)
        )
        viewModel = ViewModelProvider(this, factory)[SettingWidgetViewModel::class.java]
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(KEY_REQUEST_PLACE, this, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WidgetSettingsFagmentLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupInsets()
        setupOtherViews()
        observeViewModel()
    }

    override fun onStop() {
        WeatherWidget.updateWidget(widgetID)
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == KEY_REQUEST_PLACE) {
            val place = result.getParcelable<PlaceViewItem>(KEY_REQUEST_PLACE)
            checkNotNull(place) { "WeatherWidgetSettingListFragment wrong fragment result" }
            viewModel.onLocationTypeCertainChoice(place)
        }
    }

    private fun setupOtherViews() {
        val nightTempTextColor = requireContext().getColorCompat(R.color.widget_black_night_text)
        binding.widgetContainer.widgetNightTempTv.setTextColor(nightTempTextColor)
    }

    private fun observeViewModel() {
        viewModel.isLightTheme.observe(viewLifecycleOwner) {
            val transparency = viewModel.transparency.value
            binding.lightSettingSwitch.isChecked = it
            setupWidgetThemesColors(it, transparency)
        }
        viewModel.timeOfLastUpdate.observe(viewLifecycleOwner) {
            binding.timeSettingSwitch.isChecked = it
            val place = viewModel.widgetPlaceView.value
            if (place != null) {
                binding.widgetContainer.widgetPlaceNameTv.text = getWidgetTitle(place)
            }
        }
        viewModel.transparency.observe(viewLifecycleOwner) {
            val isLightTheme = viewModel.isLightTheme.value
            binding.transparencySeekbar.progress = ALPHA_MAX_VALUE - it
            setupWidgetThemesColors(isLightTheme, it)
        }
        viewModel.locationType.observe(viewLifecycleOwner) {
            binding.locationDescriptionTv.text = getLocationDescriptionText(null)
        }
        viewModel.widgetPlaceView.observe(viewLifecycleOwner) {
            logD("widgetPlace ${it.title}")
            restorePlace(it)
        }


    }

    private fun restorePlace(placeView: PlaceViewItem) {
        val locationDescriptionText = getLocationDescriptionText(placeView)
        binding.locationDescriptionTv.text = locationDescriptionText
        binding.widgetContainer.widgetPlaceNameTv.text = getWidgetTitle(placeView)

        val oneCallResponse = placeView.oneCallResponse

        val weatherIcon = oneCallResponse?.current?.weather?.get(0)?.getWeatherIcon()
        binding.widgetContainer.widgetWeatherIconIv.setImageResource(
            weatherIcon ?: R.drawable.ic_ovc
        )
        binding.widgetContainer.widgetTempTv.text =
            oneCallResponse?.current?.tempFormatted() ?: formatTemp(20)
        binding.widgetContainer.widgetDayTempTv.text =
            oneCallResponse?.daily?.get(0)?.temp?.dayFormatted() ?: formatTemp(18)
        binding.widgetContainer.widgetNightTempTv.text =
            oneCallResponse?.daily?.get(0)?.temp?.nightFormatted() ?: formatTemp(18)

    }

    private fun getWidgetTitle(placeView: PlaceViewItem): String {
        val showTimeUpdate = viewModel.timeOfLastUpdate.value
        return if (showTimeUpdate) {
            "${Date().format("HH:mm")}, ${placeView.title}"
        } else {
            placeView.title
        }
    }

    private fun getLocationDescriptionText(placeView: PlaceViewItem?): String? {
        return if (viewModel.locationType.value == CERTAIN) {
            placeView?.cityFullName
        } else {
            getString(R.string.current_location)
        }
    }


    private fun setupWidgetId() {
        val argKey = arguments?.getInt(WIDGET_ID_KEY)
        if (argKey != null) {
            widgetID = argKey
            return
        }
        val intent = requireActivity().intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
        }
    }

    private fun setupListeners() = with(binding) {
        lightSettingBackground.setOnClickListener {
            changeSwitcherState(lightSettingSwitch)
        }
        timeSettingBackground.setOnClickListener {
            changeSwitcherState(timeSettingSwitch)
        }
        lightSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSwitcherState(WIDGET_LIGHT_THEME, isChecked)
            viewModel.isLightTheme.value = isChecked
        }
        timeSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSwitcherState(WIDGET_SHOW_TIME_UPDATE, isChecked)
            viewModel.timeOfLastUpdate.value = isChecked
        }

        transparencySeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.saveSeekBarState(seekBar.progress)
                viewModel.transparency.value = ALPHA_MAX_VALUE - seekBar.progress
            }
        })
        locationBackground.setOnClickListener { showDialog() }
    }

    private fun setupInsets() {
        binding.widgetSettingsScrollContainer.addSystemWindowInsetToPadding(bottom = true)
    }

    private fun setupWidgetThemesColors(isLightTheme: Boolean, transparency: Int) {
        val backgroundColor = getWidgetBackground(isLightTheme, transparency)
        val backgroundTitleColor = getWidgetTitleBackground(isLightTheme, transparency)
        binding.widgetContainer.widgetPlaceNameTv.setTextColor(getTextColor(isLightTheme))
        binding.widgetContainer.widgetDayTempTv.setTextColor(getTextColor(isLightTheme))
        binding.widgetContainer.widgetTempTv.setTextColor(getTextColor(isLightTheme))
        val refreshButtonImage = getRefreshButtonImg(isLightTheme)
        binding.widgetContainer.widgetRefreshIb.setImageResource(refreshButtonImage)
        binding.widgetContainer.widgetPlaceNameContainer.background =
            ColorDrawable(backgroundTitleColor)
        binding.widgetContainer.widgetContainer.background = ColorDrawable(backgroundColor)
    }

    private fun getRefreshButtonImg(isLightTheme: Boolean): Int {
        return if (isLightTheme)
            R.drawable.ic_refresh_icon_12dp
        else
            R.drawable.ic_refresh_icon_blue_12dp
    }

    private fun getTextColor(isLightTheme: Boolean): Int {
        return if (isLightTheme)
            Color.BLACK
        else
            Color.WHITE
    }


    private fun getWidgetTitleBackground(isLightTheme: Boolean, alpha: Int): Int {
        val color = if (isLightTheme) R.color.widget_white_title else R.color.widget_black_title
        return ColorUtils.setAlphaComponent(requireContext().getColorCompat(color), alpha)
    }

    private fun getWidgetBackground(isLightTheme: Boolean, alpha: Int): Int {
        val color = if (isLightTheme) R.color.widget_white else R.color.widget_black
        return ColorUtils.setAlphaComponent(requireContext().getColorCompat(color), alpha)
    }

    private fun changeSwitcherState(switcher: SwitchMaterial) {
        val checked = switcher.isChecked
        switcher.isChecked = !checked
    }


    private fun showDialog() {
        val locationSettingDialog = LocationSettingDialogFragment()
        locationSettingDialog.onLocationTypeChoiceCallback = object : OnLocationTypeChoiceCallback {
            override fun onLocationTypeChoice(type: LocationType) {
                logD("onLocationTypeChoice ${type.ordinal}")
                when (type) {
                    CURRENT -> viewModel.onLocationCurrentChoice()
                    CERTAIN -> {
                        val navigationActivity = requireActivity() as? NavigationActivity
                        navigationActivity?.navigateToFragment(SelectPlaceFragment())
                    }
                }
            }
        }
        locationSettingDialog.show(childFragmentManager, LocationSettingDialogFragment.TAG)
    }


    fun addButtonHeightToPadding(height: Int) {
        val paddingBottom = binding.widgetSettingsScrollContainer.paddingBottom
        binding.widgetSettingsScrollContainer.updatePadding(bottom = paddingBottom + height)
    }
}