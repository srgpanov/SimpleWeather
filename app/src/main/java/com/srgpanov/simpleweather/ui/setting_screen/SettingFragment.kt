package com.srgpanov.simpleweather.ui.setting_screen

import android.Manifest
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
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.local.LocalDataSourceImpl
import com.srgpanov.simpleweather.databinding.SettingFragmentBinding
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import kotlinx.coroutines.launch


class SettingFragment : Fragment() {
    private var _binding: SettingFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var mainActivity: MainActivity?=null
    private var actionBar: ActionBar?=null
    private val repository:LocalDataSourceImpl by lazy { LocalDataSourceImpl() }
    private val PERMISSION=15

    companion object {
        const val TEMP_MEASUREMENT = "TEMP_MEASUREMENT"
        const val WIND_MEASUREMENT = "WIND_MEASUREMENT"
        const val PRESSURE_MEASUREMENT = "PRESSURE_MEASUREMENT"
        const val LOCATION_TYPE_IS_CURRENT = "LOCATION_TYPE_IS_CURRENT"
        val TAG = this::class.java.simpleName

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireActivity());
        mainActivity=requireActivity() as? MainActivity
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

    private fun setupInsets(){
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
        setupLocationType()
        setupLocationPermissionSetting()
        logD("setting ${sharedPreferences.all}")
    }

    private fun setupLocationPermissionSetting() {
        if (locationPermissionIsGranted()){
            binding.locationPermissionGroup.visibility=View.GONE
            binding.locationPermissionSwitch.isChecked=true
        }else{
            binding.locationPermissionGroup.visibility=View.VISIBLE
            binding.locationPermissionSwitch.isChecked=false
        }
    }
    private fun locationPermissionIsGranted(): Boolean {
        val permissionCoarseLocation =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val permissionFineLocation =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        logD("location permissionCoarseLocation $permissionCoarseLocation permissionFineLocation $permissionFineLocation")
        return permissionCoarseLocation == PackageManager.PERMISSION_GRANTED &&
                permissionFineLocation == PackageManager.PERMISSION_GRANTED
    }

    private fun setupLocationType() {
        val typeIsCurrent = sharedPreferences.getBoolean(LOCATION_TYPE_IS_CURRENT, true)
        if (typeIsCurrent) {
            binding.locationDescriptionTv.text = getString(R.string.current_location)
        } else {
            lifecycleScope.launch {
                val currentLocation = repository.getCurrentLocation()
                currentLocation?.let {
                    binding.locationDescriptionTv.text = currentLocation.cityFullName
                }
            }
        }
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
            requestLocationPermission(PERMISSION)
        }
        binding.locationPermissionSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                requestLocationPermission(PERMISSION)
            }
        }
    }
    private fun requestLocationPermission(requestCode: Int) {
        if (locationPermissionIsGranted()) {
            setupLocationPermissionSetting()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    requestCode
                )
            } else {
                openApplicationSettings();
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==PERMISSION){
            setupLocationPermissionSetting()
        }

    }

    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + requireContext().packageName)
        )
        startActivityForResult(appSettingsIntent, PERMISSION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PERMISSION) {
            setupLocationPermissionSetting()
            return;
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDialog() {
        val locationSettingDialog = LocationSettingDialogFragment()
        locationSettingDialog.onLocationTypeChoiceCallback= object : LocationSettingDialogFragment.OnLocationTypeChoiceCallback {
            override fun onLocationTypeChoice() {
                logD("onLocationTypeChoice")
                setupLocationType()
            }

        }
        locationSettingDialog.show(childFragmentManager,
            LocationSettingDialogFragment.TAG
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
