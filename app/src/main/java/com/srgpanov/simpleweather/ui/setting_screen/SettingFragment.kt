package com.srgpanov.simpleweather.ui.setting_screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.SettingFragmentBinding
import com.srgpanov.simpleweather.other.NavigationActivity
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.about_screen.AboutFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment.Companion.REQUEST_PLACE
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.LocationType.*
import com.srgpanov.simpleweather.ui.setting_screen.LocationSettingDialogFragment.OnLocationTypeChoiceCallback
import com.srgpanov.simpleweather.ui.setting_widget_screen.SettingWidgetFragment
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget
import kotlinx.coroutines.launch
import javax.inject.Inject


class SettingFragment : Fragment() {
    private var _binding: SettingFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var shareViewModel: ShareViewModel
    private var mainActivity: MainActivity? = null
    private var actionBar: ActionBar? = null
    @Inject lateinit var  localDataSource: LocalDataSourceImpl
    private val registerForLocationPermission =
        registerForActivityResult(RequestMultiplePermissions()) { map ->
            var permissionGranted = true
            for (entry in map.entries) {
                logD("entry.value ${entry.value}")
                if (entry.value == false) {
                    permissionGranted = false
                }
            }
            setupLocationPermissionSetting(permissionGranted)
        }
    private val registerForAppSettings = registerForActivityResult(StartActivityForResult()) {
        logD("ActivityResult $it")
        setupLocationPermissionSetting(locationPermissionIsGranted())
    }

    companion object {
        const val TEMP_MEASUREMENT = "TEMP_MEASUREMENT"
        const val WIND_MEASUREMENT = "WIND_MEASUREMENT"
        const val PRESSURE_MEASUREMENT = "PRESSURE_MEASUREMENT"
        const val LOCATION_TYPE = "LOCATION_TYPE"
        val TAG = this::class.java.simpleName

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireActivity());
        mainActivity = requireActivity() as? MainActivity
        requireActivity().supportFragmentManager.setFragmentResultListener(
            REQUEST_PLACE,
            this,
            FragmentResultListener { requestKey, result ->
                if (requestKey == REQUEST_PLACE) {
                    val place = result.getParcelable<PlaceEntity>(REQUEST_PLACE)
                    if (place != null) {
                        onCertainLocationChoice(place)
                    }
                }
            })
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            mainActivity?.onBackPressedSuper()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        mainActivity?.setSupportActionBar(binding.toolbar)
        actionBar = mainActivity?.getSupportActionBar()
        actionBar?.setTitle(mainActivity?.getString(R.string.settings))
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.statusBarView) { view, insets ->
            view.updateLayoutParams {
                if (insets.systemWindowInsetTop != 0) {
                    height = insets.systemWindowInsetTop
                }
            }
            insets
        }
        binding.statusBarView.requestApplyInsetsWhenAttached()
    }

    private fun setupOtherView() {
        val temp = sharedPreferences.getInt(TEMP_MEASUREMENT, 0)
        if (Temp.CELSIUS.value == temp) {
            binding.tempCBtn.isChecked = true
        } else {
            binding.tempFBtn.isChecked = true
        }
        val wind = sharedPreferences.getInt(WIND_MEASUREMENT, 0)
        if (Wind.M_S.value == wind) {
            binding.windMsBtn.isChecked = true
        } else {
            binding.windKmhBtn.isChecked = true
        }

        val pressure = sharedPreferences.getInt(PRESSURE_MEASUREMENT, 0)
        if (Pressure.MM_HG.value == pressure) {
            binding.pressureMmhgBtn.isChecked = true
        } else {
            binding.pressureHpaBtn.isChecked = true
        }
        lifecycleScope.launch {
            binding.locationDescriptionTv.text = getLocationTypeText()
        }
        val widgetIdArray =getWidgetsId()
        if (widgetIdArray.isNotEmpty()){
            binding.widgetSettingBackground.visibility=View.VISIBLE
            binding.widgetSettingTextTv.visibility=View.VISIBLE
        }

        logD("setting ${sharedPreferences.all}")

    }

    private fun getWidgetsId():IntArray {
        val widgetManager = AppWidgetManager.getInstance(requireActivity())
        val widgetComponent = ComponentName(requireActivity(), WeatherWidget::class.java)
        return widgetManager.getAppWidgetIds(widgetComponent)
    }

    private fun setupLocationPermissionSetting(permissionsGranted: Boolean) {
        if (permissionsGranted) {
            binding.locationPermissionGroup.visibility = View.GONE
            binding.locationPermissionSwitch.isChecked = true
        } else {
            binding.locationPermissionGroup.visibility = View.VISIBLE
            binding.locationPermissionSwitch.isChecked = false
        }
    }

    private fun locationPermissionIsGranted(): Boolean {
        val permissionCoarseLocation =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_COARSE_LOCATION
            )
        val permissionFineLocation =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_FINE_LOCATION
            )
        logD("location permissionCoarseLocation $permissionCoarseLocation permissionFineLocation $permissionFineLocation")
        return permissionCoarseLocation == PackageManager.PERMISSION_GRANTED &&
                permissionFineLocation == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun getLocationTypeText(): String {
        return if (getLocationType() == CURRENT) {
            getString(R.string.current_location)
        } else {
            localDataSource.getCurrentLocation()?.cityFullName ?: ""
        }
    }


    private fun getLocationType(): LocationType {
        val int = sharedPreferences.getInt(LOCATION_TYPE, 0)
        return values()[int]
    }

    private fun setupListeners() {
        binding.tempCBtn.setOnClickListener {
            sharedPreferences.edit().putInt(
                TEMP_MEASUREMENT,
                Temp.CELSIUS.value
            ).apply()
        }
        binding.tempFBtn.setOnClickListener {
            sharedPreferences.edit().putInt(
                TEMP_MEASUREMENT,
                Temp.FAHRENHEIT.value
            ).apply()
        }
        binding.windMsBtn.setOnClickListener {
            sharedPreferences.edit().putInt(
                WIND_MEASUREMENT,
                Wind.M_S.value
            ).apply()
        }
        binding.windKmhBtn.setOnClickListener {
            sharedPreferences.edit().putInt(
                WIND_MEASUREMENT,
                Wind.KM_H.value
            ).apply()
        }
        binding.pressureMmhgBtn.setOnClickListener {
            sharedPreferences.edit().putInt(
                PRESSURE_MEASUREMENT,
                Pressure.MM_HG.value
            ).apply()
        }
        binding.pressureHpaBtn.setOnClickListener {
            sharedPreferences.edit().putInt(
                PRESSURE_MEASUREMENT,
                Pressure.H_PA.value
            ).apply()
        }
        binding.locationBackground.setOnClickListener {
            showDialog()
        }
        binding.locationPermissionBackground.setOnClickListener {
            requestLocationPermission()
        }
        binding.locationPermissionSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                requestLocationPermission()
            }
        }
        binding.widgetSettingBackground.setOnClickListener {
            val navigationActivity = requireActivity() as? NavigationActivity
            navigationActivity?.navigateToFragment(SettingWidgetFragment())
        }
        binding.aboutBackground.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.container,AboutFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun requestLocationPermission() {
        if (locationPermissionIsGranted()) {
            setupLocationPermissionSetting(true)
        } else {
            logD("locationPermission not Granted")
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                registerForLocationPermission.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                logD("openApplicationSettings")
                openApplicationSettings();
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
                    CERTAIN -> mainActivity?.navigate(SelectPlaceFragment::class.java)
                }
                logD("onLocationTypeChoice")
            }
        }
        locationSettingDialog.show(
            childFragmentManager,
            LocationSettingDialogFragment.TAG
        )
    }

    private fun onCurrentLocationChoice() {
        binding.locationDescriptionTv.text = getString(R.string.current_location)
        sharedPreferences.edit().putInt(LOCATION_TYPE, CURRENT.ordinal).apply()
    }

    private fun onCertainLocationChoice(placeEntity: PlaceEntity) {
        lifecycleScope.launch {
            sharedPreferences.edit().putInt(LOCATION_TYPE, CERTAIN.ordinal).apply()
            localDataSource.savePlace(placeEntity)
            localDataSource.savePlaceToHistory(placeEntity)
            localDataSource.saveCurrentPlace(placeEntity.toCurrentTable())
            binding.locationDescriptionTv.text = localDataSource.getCurrentLocation()?.cityFullName ?: ""
            shareViewModel.weatherPlace.value = placeEntity
        }
    }

    override fun onDestroyView() {
        _binding = null
        actionBar=null
        mainActivity?.setSupportActionBar(null)
        super.onDestroyView()
    }
}
