package com.srgpanov.simpleweather.ui.weather_screen

import android.Manifest
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.DetailFragmentBinding
import com.srgpanov.simpleweather.di.ArgumentsViewModelFactory
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.forecast_screen.ForecastPagerFragment
import com.srgpanov.simpleweather.ui.setting_screen.SettingFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailViewModel.Companion.ARGUMENT_PLACE
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget.Companion.ACTION_SHOW_WEATHER
import com.srgpanov.simpleweather.ui.weather_widget.WeatherWidget.Companion.PLACE_ENTITY_KEY
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class DetailFragment : Fragment() {
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private var mainActivity: MainActivity? = null
    private val parentJob = Job()
    private val scope = CoroutineScope(coroutineContext)

    @Inject
    internal lateinit var detailViewModelFactory: DetailViewModel.DetailViewModelFactory
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var viewModel: DetailViewModel

    private var errorPanelIsVisible = false
    private var errorLayoutTranslationY: Float = 0F
    private var scrollDistancePx: Int = 0
    private var toolbarHeight: Int = 100

    private val weatherAdapter: WeatherAdapter by lazy { WeatherAdapter() }
    private var loadingDelay: Job? = null
    private var updateRvJob: Job? = null

    companion object {
        const val ANOTHER_REQUEST_LOCATION_PERMISSION = 11
        const val FIRST_REQUEST_LOCATION_PERMISSION = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectDetailFragment(this)
        shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        val placeEntity = getStartPlace(requireActivity().intent)
        val factory = ArgumentsViewModelFactory<DetailViewModel>(
            detailViewModelFactory,
            createBundle(placeEntity)
        )
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]
        mainActivity = requireActivity() as MainActivity
        logD("lifecycle onCreate  $this")
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
        if (viewModel.currentPlace.value == null) {
            viewModel.setCurrentPlace()
        }
    }


    fun updateRV() {
        logD("updateRvJob updateRV")
        updateRvJob?.start()
        updateRvJob = null
    }


    override fun onDestroyView() {
        binding.detailRv.adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    fun onBackPressed() {
        logD("back fragment")
        mainActivity?.onBackPressedSuper()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FIRST_REQUEST_LOCATION_PERMISSION, ANOTHER_REQUEST_LOCATION_PERMISSION -> {
                viewModel.setCurrentPlace()
            }
        }
    }

    private fun animateScroll(progress: Float) {
        binding.toolbar.alpha = progress
        binding.statusBackground.alpha = progress
        binding.toolbarCityTitle.setTextColor(
            blendColors(
                Color.WHITE, Color.BLACK, progress
            )
        )

        binding.burgerButton.setColorFilter(
            blendColors(
                Color.WHITE, Color.parseColor("#FF7E00"), progress
            )
        )
        binding.settingButton.setColorFilter(
            blendColors(
                Color.WHITE, Color.parseColor("#FF7E00"), progress
            )
        )
        binding.backButton.setColorFilter(
            blendColors(
                Color.WHITE, Color.parseColor("#FF7E00"), progress
            )
        )
        binding.favoriteCheckBox.setColorFilter(
            blendColors(
                Color.WHITE, Color.parseColor("#FF7E00"), progress
            )
        )
    }

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r: Float = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g: Float = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b: Float = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }


    private fun getStartPlace(activityIntent: Intent?): PlaceEntity? {
        val extras = activityIntent?.extras
        val action = requireActivity().intent.action
        if (action != null && action.equals(ACTION_SHOW_WEATHER, true)) {
            if (extras != null) {
                val intentPlace = extras.getParcelable<PlaceEntity>(PLACE_ENTITY_KEY)
                logD("intentPlace $intentPlace")
                requireActivity().intent.action = ""
                intentPlace?.favorite = true
                return intentPlace
            }
        }
        val placeEntity = arguments?.getParcelable<PlaceEntity>("place")
        logD("argPlace  ${placeEntity?.title}")
        return placeEntity
    }


    private fun showLoading(value: Boolean) {
        if (value) {
            loadingDelay?.cancel()
            loadingDelay = lifecycleScope.launch {
                delay(200)
                binding.swipeLayout.isRefreshing = value
            }
        } else {
            loadingDelay?.cancel()
            binding.swipeLayout.isRefreshing = value
        }
    }

    private fun setWeatherToFavoriteScreen(it: WeatherState?) {
        val place = shareViewModel.currentPlace.value
        if (it is WeatherState.ActualWeather) {
            place?.oneCallResponse = it.oneCallResponse

        } else {
            place?.oneCallResponse = null
        }
        shareViewModel.currentPlace.value = place
    }

    @Suppress("DEPRECATION")
    private fun requestLocationPermission(requestCode: Int) {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            requestCode
        )
    }

    private fun setupFavoriteState(showSetting: Boolean) {
        logD("showSetting $showSetting")
        return when (showSetting) {
            true -> {
                binding.burgerButton.visibility = View.VISIBLE
                binding.settingButton.visibility = View.VISIBLE
                binding.favoriteCheckBox.visibility = View.INVISIBLE
                binding.backButton.visibility = View.INVISIBLE
            }
            false -> {
                binding.favoriteCheckBox.visibility = View.VISIBLE
                binding.backButton.visibility = View.VISIBLE
                binding.settingButton.visibility = View.INVISIBLE
                binding.burgerButton.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupInsets() {
        binding.detailRv.addSystemWindowInsetToPadding(bottom = true)
        val insetsView = binding.insetsErrorView
        ViewCompat.setOnApplyWindowInsetsListener(insetsView) { view, insets ->
            view.updateLayoutParams {
                height = insets.systemWindowInsetBottom
                binding.insetsErrorView.height.also { logD("translation ${insets.systemWindowInsetBottom}") }
            }
            insets
        }
        insetsView.requestApplyInsetsWhenAttached()
        val statusView = binding.statusBackground
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

    private fun setupOtherView() {
        binding.connectionErrorButton.setOnClickListener {
            viewModel.onErrorConnectionClick()
            showErrorLayout(false)
        }
        binding.swipeLayout.setOnRefreshListener {
            viewModel.fetchFreshWeather()
            shareViewModel.refreshWeather.value = Unit
        }
        binding.swipeLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        binding.detailConstraintLayout.doOnPreDraw {
            errorLayoutTranslationY =
                (binding.connectionErrorTv.height + binding.insetsErrorView.height).toFloat()
                    .also { logD("translation $it") }
            binding.insetsErrorView.translationY = errorLayoutTranslationY
            binding.connectionErrorButton.translationY = errorLayoutTranslationY
            binding.connectionErrorTv.translationY = errorLayoutTranslationY
        }

        errorPanelIsVisible = false

    }

    private fun setupRecyclerView() {
        binding.detailRv.adapter = weatherAdapter
        weatherAdapter.scope = scope
        weatherAdapter.clickListener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                val weatherState = viewModel.weatherData.value
                if (weatherState is WeatherState.ActualWeather) {
                    val bundle = Bundle().apply {
                        putInt("position", position - 1)
                        putParcelable("oneCall", weatherState.oneCallResponse)
                    }
                    val TAG = ForecastPagerFragment::class.java.simpleName
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            ForecastPagerFragment::class.java,
                            bundle,
                            TAG
                        )
                        .addToBackStack(TAG)
                        .commit()
                }
            }
        }
        val divider = CustomWeatherItemDecoration(requireActivity())
        binding.detailRv.addItemDecoration(divider)
        //        binding.detailRv.adapter = weatherAdapter
        binding.detailRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (scrollDistancePx >= 0) scrollDistancePx += dy else scrollDistancePx = 0
                val progress = scrollDistancePx.toFloat() / toolbarHeight
                when (progress >= 1) {
                    true -> animateScroll(1f)
                    false -> if (progress >= 0) animateScroll(progress) else animateScroll(0f)
                }
            }
        })
    }

    private fun setupToolbar() {
        binding.toolbar.doOnPreDraw { toolbarHeight = binding.toolbar.height }
        binding.burgerButton.setOnClickListener {
            mainActivity?.navigateToFavoriteFragment()
        }

        binding.favoriteCheckBox.setOnClickListener {
            val isChecked = binding.favoriteCheckBox.isChecked
            viewModel.changeFavoriteStatus(isChecked)
        }
        binding.backButton.setOnClickListener {
            mainActivity?.onBackPressed()
        }
        binding.settingButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //todo доделать красивую анимацию,
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_left,
                        R.anim.slide_out_right, R.anim.slide_in_right
                    )
                    .replace(R.id.container, SettingFragment(), SettingFragment.TAG)
                    .addToBackStack(SettingFragment.TAG)
                    .commit()
            }
        })
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(viewLifecycleOwner, Observer { it ->
            if (it != null) {
                weatherAdapter.setWeather(it)
                setWeatherToFavoriteScreen(it)
            }
        })
        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            showLoading(it)
        })

        viewModel.navEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                val fragment = it
                val buildFragment = fragment.buildFragment().javaClass
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, buildFragment, null, fragment.javaClass.simpleName)
                    .commit()
            }
        })
        viewModel.weatherPlace.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.toolbarCityTitle.text = it.title
            }
        })

        viewModel.currentPlace.observe(viewLifecycleOwner, Observer {
            shareViewModel.currentPlace.value = it
        })

        viewModel.showSetting.observe(viewLifecycleOwner, Observer {
            it?.let {
                setupFavoriteState(it)
            }
        })
        viewModel.showSnackBar.observe(viewLifecycleOwner, Observer {
            val snackbar = Snackbar.make(
                binding.root,
                it ?: getString(R.string.something_goes_wrong),
                Snackbar.LENGTH_SHORT
            )
            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackbar.show()
        })
        shareViewModel.weatherPlace.observe(viewLifecycleOwner, Observer { it ->
            logD("updateRvJob weatherPlace.observe")
            updateRvJob = lifecycleScope.launch(start = CoroutineStart.LAZY) {
                viewModel.weatherPlace.value = it
            }
        })
        viewModel.errorConnectionSnackBar.observe(viewLifecycleOwner, Observer { show ->
            show?.let {
                showErrorLayout(show)
            }
        })
        viewModel.requestLocationPermission.observe(viewLifecycleOwner, Observer {
            showRequestPermissionDialog()
        })
    }

    private fun showErrorLayout(isShow: Boolean) {
        logD("error visibility $isShow errorPanelIsVisible$errorPanelIsVisible ")
        if (isShow != errorPanelIsVisible) {
            val animator = if (isShow) {
                ValueAnimator.ofFloat(errorLayoutTranslationY, 0f)
            } else {
                ValueAnimator.ofFloat(0f, errorLayoutTranslationY)
            }

            animator.duration = 350
            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(animation: ValueAnimator?) {
                    binding.insetsErrorView.translationY = animation?.animatedValue as Float
                    binding.connectionErrorButton.translationY =
                        animation.animatedValue as Float
                    binding.connectionErrorTv.translationY = animation.animatedValue as Float
                }
            })
            animator.addListener(
                onEnd = {
                    if (!errorPanelIsVisible) {
                        logD("error visibility onEnd ")
                        binding.errorGroup.visibility = View.GONE
                    }
                },
                onStart = {
                    if (isShow) {
                        binding.errorGroup.visibility = View.VISIBLE
                    }
                }
            )
            animator.start()
        }
        errorPanelIsVisible = isShow
    }


    private fun showRequestPermissionDialog() {
        val messageDialogFragment = RequestPermissionDialogFragment()
        messageDialogFragment.onClickListener = DialogInterface.OnClickListener { _, _ ->
            requestLocationPermission(FIRST_REQUEST_LOCATION_PERMISSION)
        }
        messageDialogFragment.isCancelable = false
        messageDialogFragment.show(childFragmentManager, RequestPermissionDialogFragment.TAG)

    }

    private fun createBundle(placeEntity: PlaceEntity?): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(ARGUMENT_PLACE, placeEntity)
        return bundle
    }


}

