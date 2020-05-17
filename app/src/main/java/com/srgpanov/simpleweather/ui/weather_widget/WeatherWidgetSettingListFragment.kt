package com.srgpanov.simpleweather.ui.weather_widget

import android.appwidget.AppWidgetManager
import android.content.SharedPreferences
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
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.models.other.GeoPoint
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.databinding.WidgetSettingsFagmentLayoutBinding
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment.Companion.REQUEST_PLACE
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.*
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.OnLocationTypeChoiceCallback
import kotlinx.coroutines.launch

class WeatherWidgetSettingListFragment : Fragment() {
    private var _binding: WidgetSettingsFagmentLayoutBinding? = null
    private val binding get() = _binding!!
    lateinit var preferences: SharedPreferences
    private val repository = DataRepositoryImpl()
    var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private val ALPHA_MAX_VALUE = 255
    private var transparency: Int = 0
    private var isLightTheme: Boolean = false
    private var timeOfLastUpdate: String?=""
    private var locationName: String = ""
    private var locationTitle: String? = ""

    companion object {
        val TAG = WeatherWidgetSettingListFragment::class.java.simpleName
        val WIDGET_ID_KEY = "WIDGET_ID_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        logD("FragmentResultListener $parentFragmentManager")
        requireActivity().supportFragmentManager.setFragmentResultListener(
            REQUEST_PLACE,
            this,
            FragmentResultListener { requestKey, result ->
                logD("FragmentResultListener $requestKey $result")
                if (requestKey == REQUEST_PLACE) {
                    val place = result.getParcelable<PlaceEntity>(REQUEST_PLACE)
                    logD("FragmentResultListener $place")
                    if (place != null) {
                        onLocationTypeOtherChoice(place)
                        lifecycleScope.launch {
                            repository.savePlace(place)
                            repository.savePlaceToHistory(place)
                        }
                    } else {
                        logE("place != null somethings goes wrong")
                    }
                }
            })
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
        setupWidgetId()
        setupListeners()
        setupInsets()
        restoreSettings()
    }

    override fun onStop() {
        WeatherWidget.updateWidget(requireContext().applicationContext,widgetID)
        super.onStop()
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
        binding.iconsSettingBackground.setOnClickListener {
            changeSwitcherState(binding.iconsSettingSwitch)
        }
        binding.lightSettingBackground.setOnClickListener {
            changeSwitcherState(binding.lightSettingSwitch)
        }
        binding.timeSettingBackground.setOnClickListener {
            changeSwitcherState(binding.timeSettingSwitch)
        }
        binding.iconsSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            saveSwitcherState(WIDGET_ICONS, isChecked)
        }
        binding.lightSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isLightTheme=isChecked
            saveSwitcherState(WIDGET_LIGHT_THEME, isChecked)
            setupWidgetThemesColors(isChecked, transparency)
        }
        binding.timeSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            saveSwitcherState(WIDGET_SHOW_TIME_UPDATE, isChecked)
            val formattedTimeOfLastUpdate =formatTimeOfUpdate(isChecked)
            val title ="$formattedTimeOfLastUpdate$locationName"
            binding.widgetContainer.widgetPlaceNameTv.text = title
        }

        binding.transparencySeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                preferences.edit()
                    .putInt(WIDGET_TRANSPARENCY + widgetID, ALPHA_MAX_VALUE - seekBar.progress)
                    .apply()
                transparency = ALPHA_MAX_VALUE - seekBar.progress
                setupWidgetThemesColors(isLightTheme, transparency)
            }
        })
        binding.locationBackground.setOnClickListener {
            showDialog()
        }

    }


    private fun setupInsets() {
        binding.widgetSettingsScrollContainer.addSystemWindowInsetToPadding(bottom = true)
    }

    private fun restoreSettings() {
        binding.iconsSettingSwitch.isChecked = getSwitchState(WIDGET_ICONS)
        binding.lightSettingSwitch.isChecked = getSwitchState(WIDGET_LIGHT_THEME)
        binding.timeSettingSwitch.isChecked = getSwitchState(WIDGET_SHOW_TIME_UPDATE)
        binding.transparencySeekbar.progress =
            ALPHA_MAX_VALUE-preferences.getInt(WIDGET_TRANSPARENCY + widgetID, ALPHA_MAX_VALUE)
        binding.locationDescriptionTv.text = getLocationDescriptionText()
        val nightTempTextColor =
            ContextCompat.getColor(requireContext(), R.color.widget_black_night_text)
        binding.widgetContainer.widgetNightTempTv.setTextColor(nightTempTextColor)
        isLightTheme = preferences.getBoolean(WIDGET_LIGHT_THEME + widgetID, false)
        transparency = preferences.getInt(WIDGET_TRANSPARENCY + widgetID, ALPHA_MAX_VALUE)
        setupWidgetThemesColors(isLightTheme, transparency)
        val showTimeUpdate = preferences.getBoolean(WIDGET_SHOW_TIME_UPDATE + widgetID, false)
        val locationType = preferences.getInt(WIDGET_LOCATION_TYPE + widgetID, CURRENT.ordinal)
        lifecycleScope.launch {
            val locationProvider = LocationProvider(values()[locationType])
            val shownGeoPoint =
                if (locationType == CURRENT.ordinal) {
                    locationProvider.getGeoPoint()
                } else {
                    val locationLatitude: Double =
                        WeatherWidget.getCoordinate(WIDGET_LATITUDE + widgetID, preferences)
                    val locationLongitude: Double =
                        WeatherWidget.getCoordinate(WIDGET_LONGITUDE + widgetID, preferences)
                    GeoPoint(locationLatitude, locationLongitude)
                }
            bindTitleWidgetView(locationType, shownGeoPoint, showTimeUpdate)
            val weatherResponse = shownGeoPoint?.let { repository.getOneCallTable(it) }
            weatherResponse?.oneCallResponse?.let { oneCallResponse ->
                bindWeatherToWidgetView(oneCallResponse)
            }
        }
    }

    private fun setupWidgetThemesColors(isLightTheme: Boolean, transparency: Int) {
        val backgroundColor = getWidgetBackground(isLightTheme, transparency)
        val backgroundTitleColor = getWidgetTitleBackground(isLightTheme, transparency)
        binding.widgetContainer.widgetPlaceNameTv.setTextColor(getTextColor(isLightTheme))
        binding.widgetContainer.widgetDayTempTv.setTextColor(getTextColor(isLightTheme))
        binding.widgetContainer.widgetTempTv.setTextColor(getTextColor(isLightTheme))
        val refreshButtonImage = getrefreshButtonImg(isLightTheme)
        binding.widgetContainer.widgetRefreshIb.setImageResource(refreshButtonImage)
        binding.widgetContainer.widgetPlaceNameContainer.background =
            ColorDrawable(backgroundTitleColor)
        binding.widgetContainer.widgetContainer.background = ColorDrawable(backgroundColor)
    }

    private fun getrefreshButtonImg(isLightTheme: Boolean) =
        if (isLightTheme) R.drawable.ic_refresh_icon_12dp else R.drawable.ic_refresh_icon_blue_12dp

    private fun getTextColor(isLightTheme: Boolean) = if (isLightTheme) Color.BLACK else Color.WHITE

    private suspend fun bindTitleWidgetView(
        locationType: Int,
        shownGeoPoint: GeoPoint?,
        showTimeUpdate: Boolean
    ) {
        locationTitle = preferences.getString(WIDGET_LOCATION_NAME + widgetID, "")
        locationName = if (locationType == CURRENT.ordinal) {
            WeatherWidget.getLocationName(shownGeoPoint, requireContext())
        } else {
            locationTitle ?: requireContext().getString(R.string.refresh_required)
        }
        timeOfLastUpdate =WeatherWidget.getTimeOfLastUpdate(repository, shownGeoPoint)
        val formattedTimeOfLastUpdate =formatTimeOfUpdate(showTimeUpdate)
        val title ="$formattedTimeOfLastUpdate$locationName"
        binding.widgetContainer.widgetPlaceNameTv.text = title
    }

    private fun formatTimeOfUpdate(showTimeUpdate: Boolean): String {
        return if (showTimeUpdate) {
            val comma = if (timeOfLastUpdate != null) ", " else ""
            "${timeOfLastUpdate ?: ""}$comma "
        } else {
            ""
        }
    }

    private fun bindWeatherToWidgetView(oneCallResponse: OneCallResponse) {
        val tempCurrent = oneCallResponse.current.tempFormatted()
        val tempDay = oneCallResponse.daily[0].temp.dayFormated()
        val tempNight = oneCallResponse.daily[0].temp.nightFormated()
        val weatherIcon = oneCallResponse.current.weather[0].getWeatherIcon()
        binding.widgetContainer.widgetTempTv.text = tempCurrent
        binding.widgetContainer.widgetDayTempTv.text = tempDay
        binding.widgetContainer.widgetNightTempTv.text = tempNight
        binding.widgetContainer.widgetWeatherIconIv.setImageResource(weatherIcon)
    }

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

    private fun saveSwitcherState(switcher: String, isChecked: Boolean) {
        preferences.edit().putBoolean(switcher + widgetID, isChecked).apply()
    }

    private fun getSwitchState(switch: String): Boolean {
        return preferences.getBoolean(switch + widgetID, false)
    }

    private fun showDialog() {
        val locationSettingDialog = LocationSettingDialogFragment()
        locationSettingDialog.onLocationTypeChoiceCallback = object :
            OnLocationTypeChoiceCallback {
            override fun onLocationTypeChoice(type: LocationType) {
                logD("onLocationTypeChoice ${type.ordinal}")
                when (type) {
                    CURRENT -> preferences.edit()
                        .putInt(WIDGET_LOCATION_TYPE + widgetID, type.ordinal).apply()
                    CERTAIN -> {
                        val navigationActivity = requireActivity() as? NavigationActivity
                        navigationActivity?.navigateToFragment(SelectPlaceFragment())
                    }
                }
                logD("podrobno ${WIDGET_LOCATION_TYPE + widgetID} ${type.ordinal}")
                binding.locationDescriptionTv.text = getLocationDescriptionText()
            }

        }
        locationSettingDialog.show(childFragmentManager, LocationSettingDialogFragment.TAG)
    }

    private fun getLocationDescriptionText(): String {
        return when (getLocationType()) {
            CURRENT -> getString(R.string.current_location)
            CERTAIN -> preferences.getString(WIDGET_LOCATION_NAME + widgetID, "Error") ?: ""
        }
    }

    private fun getLocationType(): LocationType {
        val savedType =
            preferences.getInt(
                WIDGET_LOCATION_TYPE + widgetID,
                CURRENT.ordinal
            )
        return values()[savedType]
    }

    private fun onLocationTypeOtherChoice(place: PlaceEntity) {
        preferences.edit()
            .putInt(WIDGET_LOCATION_TYPE + widgetID, CERTAIN.ordinal).apply()
        preferences.edit()
            .putString(WIDGET_LATITUDE + widgetID, place.lat.toString()).apply()
        preferences.edit()
            .putString(WIDGET_LONGITUDE + widgetID, place.lon.toString()).apply()
        preferences.edit()
            .putString(WIDGET_LOCATION_NAME + widgetID, place.title).apply()
        binding.locationDescriptionTv.text = getLocationDescriptionText()
        binding.widgetContainer.widgetPlaceNameTv.text=place.title
    }

    fun addButtonHeightToPadding(height: Int) {
        val paddingBottom = binding.widgetSettingsScrollContainer.paddingBottom
        binding.widgetSettingsScrollContainer.updatePadding(bottom = paddingBottom + height)
    }


}