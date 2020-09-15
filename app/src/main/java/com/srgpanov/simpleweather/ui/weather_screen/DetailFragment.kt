package com.srgpanov.simpleweather.ui.weather_screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.addListener
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.DetailFragmentBinding
import com.srgpanov.simpleweather.di.ArgumentsViewModelFactory
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.forecast_screen.ForecastPagerFragment
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailViewModel.Companion.ARGUMENT_PLACE
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget.Companion.ACTION_SHOW_WEATHER
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget.Companion.PLACE_ENTITY_KEY
import kotlinx.coroutines.*
import javax.inject.Inject


@ExperimentalCoroutinesApi
class DetailFragment : Fragment(), FragmentResultListener {
    private var _binding: DetailFragmentBinding? = null
    private val binding
        get() = _binding!!
    private var mainActivity: MainActivity? = null

    @Inject
    lateinit var detailViewModelFactory: DetailViewModel.DetailViewModelFactory
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var viewModel: DetailViewModel

    private var errorPanelIsVisible = false
    private var errorLayoutTranslationY: Float = 0F
    private var scrollDistancePx: Int = 0
    private var toolbarHeight: Int = 100
    private val blendedColor = Color.parseColor("#FF7E00")
    private var systemTopInset = 0

    private val weatherAdapter: WeatherAdapter by lazy { WeatherAdapter() }
    private var loadingDelay: Job? = null
    private var updateRvJob: Job? = null
    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(), ::handleRequestPermission
    )

    companion object {
        const val KEY_FAVORITE_PLACE_SELECTED = "KEY_FAVORITE_PLACE_SELECTED"
        private const val KEY_ARG_PLACE = "KEY_PLACE"
        private const val KEY_ARG_SHOW_FAVORITE_CHECKBOX = "KEY_ARG_SHOW_FAVORITE_CHECKBOX"

        fun newInstance(
            place: PlaceViewItem? = null,
            showFavoriteCheckBox: Boolean = false
        ): DetailFragment {
            return DetailFragment().apply {
                arguments = bundleOf(
                    KEY_ARG_PLACE to place,
                    KEY_ARG_SHOW_FAVORITE_CHECKBOX to showFavoriteCheckBox
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectDetailFragment(this)
        shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        val placeEntity = getStartPlace(requireActivity().intent)
        val factory: ArgumentsViewModelFactory<DetailViewModel> = ArgumentsViewModelFactory(
            detailViewModelFactory,
            bundleOf(ARGUMENT_PLACE to placeEntity)
        )
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]
        parentFragmentManager
            .setFragmentResultListener(KEY_FAVORITE_PLACE_SELECTED, this, this)
        mainActivity = requireActivity() as? MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        setupToolbar()
        setupRecyclerView()
        setupOtherView()
        observeViewModel()
    }

    fun updateRV() {
        updateRvJob?.start()
        updateRvJob = null
    }


    override fun onDestroyView() {
        binding.detailRv.adapter = null
        _binding = null
        super.onDestroyView()
    }


    fun onBackPressed() {
        mainActivity?.onBackPressedSuper()
    }

    private fun handleRequestPermission(map: Map<String, Boolean>) {
        for (entry in map.entries) {
            if (entry.key == ACCESS_FINE_LOCATION || entry.key == ACCESS_COARSE_LOCATION) {
                viewModel.setCurrentPlace()
                break
            }
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == KEY_FAVORITE_PLACE_SELECTED) {
            val place =
                result.getParcelable<PlaceViewItem>(KEY_FAVORITE_PLACE_SELECTED)
            checkNotNull(place) { "place null after select on FavoriteFragment" }
            updateRvJob = lifecycleScope.launch(start = CoroutineStart.LAZY) {
                viewModel.setCurrentPlace(place)
            }
        }
    }

    private fun animateScroll(progress: Float) {
        binding.toolbar.alpha = progress
        binding.statusBackground.alpha = progress
        val toolbarColorFilter = blendColors(Color.WHITE, Color.BLACK, progress)
        val buttonsColorFilter = blendColors(Color.WHITE, blendedColor, progress)

        binding.toolbarCityTitle.setTextColor(toolbarColorFilter)

        binding.burgerButton.setColorFilter(buttonsColorFilter)
        binding.settingButton.setColorFilter(buttonsColorFilter)
        binding.backButton.setColorFilter(buttonsColorFilter)
        binding.favoriteCheckBox.setColorFilter(buttonsColorFilter)
    }

    @Suppress("SameParameterValue")
    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r: Float = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g: Float = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b: Float = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }


    private fun getStartPlace(activityIntent: Intent?): PlaceViewItem? {
        val extras = activityIntent?.extras
        val action = requireActivity().intent.action
        if (action != null && action.equals(ACTION_SHOW_WEATHER, true)) {
            if (extras != null) {
                val intentPlace = extras.getParcelable<PlaceViewItem>(PLACE_ENTITY_KEY)
                requireActivity().intent.action = ""
                return intentPlace?.copy(favorite = true)
            }
        }
        return arguments?.getParcelable(KEY_ARG_PLACE)
    }


    private fun showLoading(show: Boolean) {
        loadingDelay?.cancel()
        if (show) {
            loadingDelay = lifecycleScope.launch {
                delay(400)
                binding.swipeLayout.isRefreshing = show
            }
        } else {
            binding.swipeLayout.isRefreshing = show
        }
    }

    private fun setupFavoriteState() {
        val showFavoriteCheckBox = arguments?.getBoolean(KEY_ARG_SHOW_FAVORITE_CHECKBOX) ?: false
        return if (showFavoriteCheckBox) {
            binding.favoriteCheckBox.visibility = View.VISIBLE
            binding.backButton.visibility = View.VISIBLE
            binding.settingButton.visibility = View.INVISIBLE
            binding.burgerButton.visibility = View.INVISIBLE
        } else {
            binding.burgerButton.visibility = View.VISIBLE
            binding.settingButton.visibility = View.VISIBLE
            binding.favoriteCheckBox.visibility = View.INVISIBLE
            binding.backButton.visibility = View.INVISIBLE
        }
    }

    private fun setupInsets() = with(binding) {
        detailRv.addSystemWindowInsetToPadding(bottom = true)
        insetsErrorView.setHeightOrWidthAsSystemWindowInset(InsetSide.BOTTOM) { insets ->
            errorLayoutTranslationY =
                (connectionErrorTv.height + insetsErrorView.height + insets).toFloat()
            insetsErrorView.translationY = errorLayoutTranslationY
            connectionErrorButton.translationY = errorLayoutTranslationY
            connectionErrorTv.translationY = errorLayoutTranslationY
        }
        if (systemTopInset == 0) {
            statusBackground.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP) { insetSize ->
                systemTopInset = insetSize
            }
        } else {
            statusBackground.updateLayoutParams { height = systemTopInset }
        }

    }

    private fun setupOtherView() = with(binding) {
        connectionErrorButton.setOnClickListener {
            viewModel.onErrorConnectionClick()
            showErrorLayout(false)
        }
        swipeLayout.setOnRefreshListener {
            viewModel.fetchFreshWeather()
            shareViewModel.refreshWeather.value = Unit
        }
        swipeLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        errorPanelIsVisible = false
        errorGroup.isVisible = errorPanelIsVisible
        setupFavoriteState()
    }

    private fun setupRecyclerView() {
        binding.detailRv.adapter = weatherAdapter
        weatherAdapter.clickListener = { position ->
            navigateToForecastScreen(position)
        }
        weatherAdapter.errorClickListener = {
            viewModel.fetchFreshWeather(true)
        }
        val divider = CustomWeatherItemDecoration(requireActivity())
        binding.detailRv.addItemDecoration(divider)
        binding.detailRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollDistancePx = if (scrollDistancePx >= 0) {
                    scrollDistancePx + dy
                } else {
                    0
                }
                val scroll = scrollDistancePx.toFloat() / toolbarHeight
                val progress = calculateProgress(scroll)
                animateScroll(progress)
            }
        })
    }

    private fun calculateProgress(scroll: Float): Float {
        return if (scroll > 1f) {
            1f
        } else {
            if (scroll < 0) {
                0f
            } else {
                scroll
            }
        }
    }

    private fun navigateToForecastScreen(position: Int) {
        val weatherState = viewModel.weatherData.value
        if (weatherState is WeatherState.ActualWeather) {
            val fragment = ForecastPagerFragment.newInstance(
                response = weatherState.weatherViewItem.oneCallResponse,
                position = position
            )
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupToolbar() = with(binding) {
        toolbar.doOnPreDraw { toolbarHeight = toolbar.height }
        burgerButton.setOnClickListener {
            mainActivity?.navigateToFavoriteFragment()
        }

        favoriteCheckBox.setOnClickListener {
            viewModel.changeFavoriteStatus(favoriteCheckBox.isChecked)
        }
        backButton.setOnClickListener {
            mainActivity?.onBackPressed()
        }
        settingButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_left, R.anim.slide_out_left,
                    R.anim.slide_out_right, R.anim.slide_in_right
                )
                .replace(R.id.container, SettingFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherState ->
            weatherAdapter.setWeather(weatherState)
        }
        viewModel.loadingState.distinctUntilChanged()
            .observe(viewLifecycleOwner) { isShow: Boolean ->
                showLoading(isShow)
            }
        viewModel.weatherPlace.observe(viewLifecycleOwner) { place: PlaceViewItem? ->
            place?.let { binding.toolbarCityTitle.text = it.title }
        }
        viewModel.favoriteCheckBox.observe(viewLifecycleOwner) { isChecked: Boolean ->
            binding.favoriteCheckBox.isChecked = isChecked
        }
        viewModel.showSnackBar.observe(viewLifecycleOwner) { strRes ->
            binding.root.showSnackBar(strRes ?: R.string.something_goes_wrong)
        }
        viewModel.errorConnectionSnackBar.observe(viewLifecycleOwner) { show ->
            show?.let { showErrorLayout(show) }
        }
        viewModel.requestLocationPermission.observe(viewLifecycleOwner) {
            showRequestPermissionDialog()
        }
    }

    private fun showErrorLayout(isShow: Boolean) {
        if (isShow != errorPanelIsVisible) {
            ValueAnimator.ofFloat(errorLayoutTranslationY, 0f).apply {
                duration = 350
                addUpdateListener {
                    binding.insetsErrorView.translationY = animatedValue()
                    binding.connectionErrorButton.translationY = animatedValue()
                    binding.connectionErrorTv.translationY = animatedValue()
                }
                addListener(
                    onEnd = {
                        if (!errorPanelIsVisible) {
                            binding.errorGroup.isVisible = false
                        }
                    },
                    onStart = {
                        if (isShow) {
                            binding.errorGroup.isVisible = true
                        }
                    }
                )
                if (isShow) start() else reverse()
            }
        }
        errorPanelIsVisible = isShow
    }

    private fun showRequestPermissionDialog() {
        val messageDialogFragment = RequestPermissionDialogFragment()
        messageDialogFragment.onClickListener = DialogInterface.OnClickListener { _, _ ->
            requestLocationPermission.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        }
        messageDialogFragment.isCancelable = false
        messageDialogFragment.show(childFragmentManager, RequestPermissionDialogFragment.TAG)
    }
}

