package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.weather.format
import com.srgpanov.simpleweather.databinding.WidgetSettingsFagmentLayoutBinding
import com.srgpanov.simpleweather.di.ArgumentsViewModelFactory
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment.Companion.REQUEST_PLACE
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CERTAIN
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.OnLocationTypeChoiceCallback
import com.srgpanov.simpleweather.ui.weather_widget.SettingWidgetViewModel.Companion.ALPHA_MAX_VALUE
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WeatherWidgetSettingListFragment : Fragment() {
    private var _binding: WidgetSettingsFagmentLayoutBinding? = null
    private val binding get() = _binding!!
    var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    @Inject
    internal lateinit var settingsViewModelFactory: SettingWidgetViewModel.SettingsListViewModelFactory
    lateinit var viewModel: SettingWidgetViewModel


    companion object {
        val TAG = WeatherWidgetSettingListFragment::class.java.simpleName
        val WIDGET_ID_KEY = "WIDGET_ID_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectWidgetSettingsListFragment(this)
        setupWidgetId()
        val factory = ArgumentsViewModelFactory<SettingWidgetViewModel>(
            settingsViewModelFactory,
            createBundle(widgetID)
        )
        viewModel = ViewModelProvider(this, factory)[SettingWidgetViewModel::class.java]
        requireActivity()
            .supportFragmentManager.setFragmentResultListener(
                REQUEST_PLACE,
                this,
                FragmentResultListener { requestKey, result ->
                    if (requestKey == REQUEST_PLACE) {
                        val place = result.getParcelable<PlaceEntity>(REQUEST_PLACE)
                        if (place != null) {
                            viewModel.onLocationTypeCertainChoice(place)
                        } else {
                            logE("place != null somethings goes wrong")
                        }
                    }
                })
    }
    private fun createBundle(widgetId:Int):Bundle{
        return Bundle().apply {
            putInt(SettingWidgetViewModel.ARGUMENT_WIDGET,widgetId)
        }
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
        WeatherWidget.updateWidget( widgetID)
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupOtherViews() {
        val nightTempTextColor =
            ContextCompat.getColor(requireContext(), R.color.widget_black_night_text)
        binding.widgetContainer.widgetNightTempTv.setTextColor(nightTempTextColor)
    }

    private fun observeViewModel() {
        viewModel.isLightTheme.observe(viewLifecycleOwner) {
            val transparency = viewModel.transparency.value
            binding.lightSettingSwitch.isChecked = it
            if ( transparency != null) {
                setupWidgetThemesColors(it, transparency)
            } else logE("isLightTheme.observe error")
        }
        viewModel.timeOfLastUpdate.observe(viewLifecycleOwner) {
            binding.timeSettingSwitch.isChecked = it
            val place = viewModel.widgetPlace.value
            if (place != null) {
                binding.widgetContainer.widgetPlaceNameTv.text=getWidgetTitle(place)
            }
        }
        viewModel.transparency.observe(viewLifecycleOwner) {
            val isLightTheme = viewModel.isLightTheme.value
            binding.transparencySeekbar.progress = ALPHA_MAX_VALUE - it
            if ( isLightTheme != null) {
                setupWidgetThemesColors(isLightTheme, it)
            }else logE("transparency.observe error")
        }
        viewModel.locationType.observe(viewLifecycleOwner) {
                binding.locationDescriptionTv.text = getLocationDescriptionText(null)
        }
        viewModel.widgetPlace.observe(viewLifecycleOwner) {
            logD("widgetPlace ${it.title}")
                restorePlace(it)
        }


    }

    private fun restorePlace(place: PlaceEntity) {
        val locationDescriptionText = getLocationDescriptionText(place)
        binding.locationDescriptionTv.text = locationDescriptionText
        binding.widgetContainer.widgetPlaceNameTv.text = getWidgetTitle(place)

        val oneCallResponse = place.oneCallResponse

        val weatherIcon = oneCallResponse?.current?.weather?.get(0)?.getWeatherIcon()
        binding.widgetContainer.widgetWeatherIconIv.setImageResource(
            weatherIcon ?: R.drawable.ic_ovc
        )
        binding.widgetContainer.widgetTempTv.text =
            oneCallResponse?.current?.tempFormatted() ?: format(20)
        binding.widgetContainer.widgetDayTempTv.text =
            oneCallResponse?.daily?.get(0)?.temp?.dayFormated() ?: format(18)
        binding.widgetContainer.widgetNightTempTv.text =
            oneCallResponse?.daily?.get(0)?.temp?.nightFormated() ?: format(18)

    }

    private fun getWidgetTitle(place: PlaceEntity): String {
        return if (viewModel.timeOfLastUpdate.value == true) {
            "${getFormattedCurrentTime()}, ${place.title}"
        } else {
            place.title
        }
    }

    private fun getLocationDescriptionText(place: PlaceEntity?): String? {
        return if (viewModel.locationType.value == CERTAIN) {
            place?.cityFullName
        } else {
            getString(R.string.current_location)
        }
    }


    private fun setupWidgetId() {
        val intent = requireActivity().intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        } else {
            val argKey = arguments?.getInt(WIDGET_ID_KEY)
            widgetID = argKey ?: AppWidgetManager.INVALID_APPWIDGET_ID
        }
        logD("widgetID ${arguments}")
        logD("widgetID $widgetID")
    }

    private fun setupListeners() {
        binding.lightSettingBackground.setOnClickListener {
            changeSwitcherState(binding.lightSettingSwitch)
        }
        binding.timeSettingBackground.setOnClickListener {
            changeSwitcherState(binding.timeSettingSwitch)
        }
        binding.lightSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSwitcherState(WIDGET_LIGHT_THEME, isChecked)
            viewModel.mutableIsLightTheme.value = isChecked
        }
        binding.timeSettingSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSwitcherState(WIDGET_SHOW_TIME_UPDATE, isChecked)
            viewModel.mutableTimeOfLastUpdate.value = isChecked
        }

        binding.transparencySeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.saveSeekBarState(seekBar.progress)
                viewModel.mutableTransparency.value = ALPHA_MAX_VALUE - seekBar.progress
            }
        })
        binding.locationBackground.setOnClickListener {
            showDialog()
        }

    }


    private fun setupInsets() {
        binding.widgetSettingsScrollContainer.addSystemWindowInsetToPadding(bottom = true)
    }


    private fun getFormattedCurrentTime() =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

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

    private fun getRefreshButtonImg(isLightTheme: Boolean) =
        if (isLightTheme) R.drawable.ic_refresh_icon_12dp else R.drawable.ic_refresh_icon_blue_12dp

    private fun getTextColor(isLightTheme: Boolean) = if (isLightTheme) Color.BLACK else Color.WHITE




    private fun getWidgetTitleBackground(isLightTheme: Boolean, alpha: Int): Int {
        return if (isLightTheme) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(requireActivity(), R.color.widget_white_title),
                alpha
            )
        } else {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(requireActivity(), R.color.widget_black_title),
                alpha
            )
        }
    }

    private fun getWidgetBackground(isLightTheme: Boolean, alpha: Int): Int {
        return if (isLightTheme) {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(requireContext(), R.color.widget_white),
                alpha
            )
        } else {
            ColorUtils.setAlphaComponent(
                ContextCompat.getColor(requireContext(), R.color.widget_black),
                alpha.also { logD("alpha $it") }
            )
        }
    }

    private fun changeSwitcherState(switcher: Switch) {
        val checked = switcher.isChecked
        switcher.isChecked = !checked
    }


    private fun showDialog() {
        val locationSettingDialog = LocationSettingDialogFragment()
        locationSettingDialog.onLocationTypeChoiceCallback = object :OnLocationTypeChoiceCallback {
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