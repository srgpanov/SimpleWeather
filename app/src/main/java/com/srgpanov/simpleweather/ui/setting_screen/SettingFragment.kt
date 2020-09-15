package com.srgpanov.simpleweather.ui.setting_screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.ActionBar
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.PreferencesStorage
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.LOCATION_TYPE
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.PRESSURE_MEASUREMENT
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.TEMP_MEASUREMENT
import com.srgpanov.simpleweather.data.PreferencesStorage.Companion.WIND_MEASUREMENT
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.databinding.SettingFragmentBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.about_screen.AboutFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment.Companion.KEY_REQUEST_PLACE
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CERTAIN
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.CURRENT
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.OnLocationTypeChoiceCallback
import com.srgpanov.simpleweather.ui.setting_widget_screen.SettingWidgetFragment
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
class SettingFragment : Fragment(), FragmentResultListener {
    private var _binding: SettingFragmentBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var localDataSource: LocalDataSourceImpl

    private var preferences: PreferencesStorage = PreferencesStorage(App.instance)
    private var temperature: Int by preferences(TEMP_MEASUREMENT, Temp.CELSIUS.value)
    private var wind: Int by preferences(WIND_MEASUREMENT, Wind.M_S.value)
    private var pressure: Int by preferences(PRESSURE_MEASUREMENT, Pressure.MM_HG.value)
    private var locationType: Int by preferences(LOCATION_TYPE, CURRENT.ordinal)
    private var mainActivity: MainActivity? = null

    private var actionBar: ActionBar? = null
    private var locationJob: Job? = null

    private val registerForLocationPermission =
        registerForActivityResult(RequestMultiplePermissions(), ::handleRequestPermission)


    private val registerForAppSettings = registerForActivityResult(StartActivityForResult()) {
        setupLocationPermissionSetting(locationPermissionIsGranted())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectSettingsFragment(this)
        mainActivity = requireActivity() as? MainActivity
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(KEY_REQUEST_PLACE, this, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SettingFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupInsets()
        setupOtherView()
        setupToolbar()
        setupLocationPermissionSetting(locationPermissionIsGranted())

    }

    override fun onStop() {
        super.onStop()
        registerForLocationPermission.unregister()
        registerForAppSettings.unregister()
    }

    override fun onDestroyView() {
        _binding = null
        actionBar = null
        mainActivity?.setSupportActionBar(null)
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            parentFragmentManager.popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == KEY_REQUEST_PLACE) {
            val place = result.getParcelable<PlaceViewItem>(KEY_REQUEST_PLACE)
            if (place != null) {
                onCertainLocationChoice(place)
            }
        }
    }

    private fun setupToolbar() {
        mainActivity?.setSupportActionBar(binding.toolbar)
        actionBar = mainActivity?.supportActionBar
        actionBar?.title = mainActivity?.getString(R.string.settings)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun setupInsets() {
        binding.statusBarView.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP)
    }

    private fun setupOtherView() {
        val isCelsius = temperature == Temp.CELSIUS.value
        binding.tempCBtn.isChecked = isCelsius
        binding.tempFBtn.isChecked = !isCelsius

        val isWindMS = wind == Wind.M_S.value
        binding.windMsBtn.isChecked = isWindMS
        binding.windKmhBtn.isChecked = !isWindMS

        val isMMHG = pressure == Pressure.MM_HG.value
        binding.pressureMmhgBtn.isChecked = isMMHG
        binding.pressureHpaBtn.isChecked = !isMMHG

        setupLocationTypeText()

        val widgetIdArray = getWidgetsId()
        if (widgetIdArray.isNotEmpty()) {
            binding.widgetSettingBackground.isInvisible = false
            binding.widgetSettingTextTv.isInvisible = false
        }
    }

    private fun getWidgetsId(): IntArray {
        val widgetManager = AppWidgetManager.getInstance(requireActivity())
        val widgetComponent = ComponentName(requireActivity(), WeatherWidget::class.java)
        return widgetManager.getAppWidgetIds(widgetComponent)
    }

    private fun setupLocationPermissionSetting(permissionsGranted: Boolean) {
        binding.locationPermissionGroup.isVisible = !permissionsGranted
        binding.locationPermissionSwitch.isChecked = permissionsGranted
    }

    private fun handleRequestPermission(map: Map<String, Boolean>) {
        var permissionGranted = true
        for (entry in map.entries) {
            if (!entry.value) {
                permissionGranted = false
            }
        }
        setupLocationPermissionSetting(permissionGranted)
    }

    private fun locationPermissionIsGranted(): Boolean {
        val coarseLocationGranted = requireActivity().checkPermission(ACCESS_COARSE_LOCATION)
        val fineLocationGranted = requireActivity().checkPermission(ACCESS_FINE_LOCATION)
        return coarseLocationGranted && fineLocationGranted
    }

    private fun setupLocationTypeText() {
        if (locationType == CURRENT.ordinal) {
            locationJob?.cancel()
            binding.locationDescriptionTv.text = getString(R.string.current_location)
        } else {
            observeCurrentLocation()
        }
    }

    private fun observeCurrentLocation() {
        locationJob?.cancel()
        locationJob = localDataSource
            .getCurrentLocationFlow()
            .flowOn(Dispatchers.IO)
            .collectIn(lifecycleScope) {
                binding.locationDescriptionTv.text = it?.cityFullName ?: it?.title
            }

    }


    private fun setupListeners() {
        binding.tempCBtn.setOnClickListener { temperature = Temp.CELSIUS.value }
        binding.tempFBtn.setOnClickListener { temperature = Temp.FAHRENHEIT.value }

        binding.windMsBtn.setOnClickListener { wind = Wind.M_S.value }
        binding.windKmhBtn.setOnClickListener { wind = Wind.KM_H.value }

        binding.pressureMmhgBtn.setOnClickListener { pressure = Pressure.MM_HG.value }
        binding.pressureHpaBtn.setOnClickListener { pressure = Pressure.H_PA.value }

        binding.locationBackground.setOnClickListener {
            showDialog()
        }
        binding.locationPermissionBackground.setOnClickListener {
            requestLocationPermission()
        }
        binding.locationPermissionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) requestLocationPermission()
        }
        binding.widgetSettingBackground.setOnClickListener {
            val navigationActivity = requireActivity() as? NavigationActivity
            navigationActivity?.navigateToFragment(SettingWidgetFragment())
        }
        binding.aboutBackground.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                )
                .replace(R.id.container, AboutFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun requestLocationPermission() {
        if (locationPermissionIsGranted()) {
            setupLocationPermissionSetting(true)
        } else {
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                registerForLocationPermission.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                openApplicationSettings()
            }
        }
    }


    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + requireContext().packageName)
        )
        registerForAppSettings.launch(appSettingsIntent)
    }


    private fun showDialog() {
        val locationSettingDialog = LocationSettingDialogFragment()
        locationSettingDialog.onLocationTypeChoiceCallback = object : OnLocationTypeChoiceCallback {
            override fun onLocationTypeChoice(type: LocationType) {
                when (type) {
                    CURRENT -> onCurrentLocationChoice()
                    CERTAIN -> selectPlace()
                }
            }
        }
        locationSettingDialog.show(
            childFragmentManager,
            LocationSettingDialogFragment.TAG
        )
    }

    private fun selectPlace() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, SelectPlaceFragment(), SelectPlaceFragment.TAG)
            .addToBackStack(SelectPlaceFragment.TAG)
            .commit()
    }

    private fun onCurrentLocationChoice() {
        binding.locationDescriptionTv.text = getString(R.string.current_location)
        locationType = CURRENT.ordinal
    }

    private fun onCertainLocationChoice(placeViewItem: PlaceViewItem) {
        lifecycleScope.launch {
            localDataSource.savePlace(placeViewItem)
            localDataSource.savePlaceToHistory(placeViewItem)
            localDataSource.saveCurrentPlace(placeViewItem.toCurrentEntity())
            locationType = CERTAIN.ordinal
            setupLocationTypeText()
        }
    }


}
