package com.srgpanov.simpleweather.ui.weather_screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.data.entity.weather.WeatherResponse
import com.srgpanov.simpleweather.databinding.DetailFragmentBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteFragment
import com.srgpanov.simpleweather.ui.forecast_screen.ForecastPagerFragment
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private lateinit var shareViewModel:ShareViewModel
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val parentJob = Job()
    private lateinit var weatherAdapter: WeatherAdapter
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private var scrollDistancePx:Int = 0
    private var toolbarHeight:Int=100


    companion object {
        fun newInstance() = DetailFragment()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailFragmentBinding.inflate(layoutInflater,container,false)
        prepareViews()
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        shareViewModel=ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
        viewModel.weatherData.observe(viewLifecycleOwner, Observer {
            logD("new data $it")
            binding.swipeLayout.isRefreshing=false
            refreshRecycler(it)
            binding.toolbarCityTitle.text=shareViewModel.featureMember.value?.GeoObject?.name
        })
        shareViewModel.featureMember.observe(viewLifecycleOwner, Observer {
            logD("new geoPoint $it")
            scope.launch {
                delay(3000)
                val point = it.GeoObject.Point.getGeoPoint()
                viewModel.fetchWeather(point.lat,point.lon)
            }
        })

        viewModel.readDB()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun prepareViews() {
        binding.swipeLayout.setOnRefreshListener{ viewModel.fetchWeather() }
        binding.swipeLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        setupInsets()
        setupToolbar()
        setupRecyclerView()
    }
    private fun setupInsets() {
        binding.detailRv.addSystemWindowInsetToPadding(bottom = true)
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
    private fun setupRecyclerView() {
        weatherAdapter = WeatherAdapter()
        weatherAdapter.clickListener= object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                val request=viewModel.weatherData.value
                shareViewModel.request=request
                shareViewModel.daySelected=position-1
                (activity as MainActivity).navigate(ForecastPagerFragment::class.java)
            }
        }
        val divider =CustomWeatherItemDecoration(requireActivity())
        binding.detailRv.apply {
            layoutManager = LinearLayoutManager(activity)
            binding.detailRv.adapter = weatherAdapter
            this.addItemDecoration(divider)
        }
        binding.detailRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (scrollDistancePx>=0) scrollDistancePx+=dy else scrollDistancePx=0
                val progress=scrollDistancePx.toFloat()/toolbarHeight
                when(progress>=1){
                    true -> animateScroll(1f)
                    false -> if(progress>=0)animateScroll(progress) else animateScroll(0f)
                }
            }
        })
    }
    private fun animateScroll(progress:Float){
        binding.toolbar.alpha=progress
        binding.statusBackground.alpha=progress
        binding.toolbarCityTitle.setTextColor(blendColors(
            Color.WHITE,Color.BLACK,progress
        ))
        binding.burgerButton.setColorFilter(blendColors(
            Color.WHITE,Color.parseColor("#FF7E00"),progress
        ))
        binding.settingButton.setColorFilter(blendColors(
            Color.WHITE,Color.parseColor("#FF7E00"),progress
        ))

    }
    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r: Float = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g: Float = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b: Float = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
    private fun setupToolbar() {
        binding.toolbar.doOnPreDraw { toolbarHeight=binding.toolbar.height}
        binding.burgerButton.setOnClickListener {
            (activity as MainActivity).navigate(FavoriteFragment::class.java)
        }
    }
    private fun refreshRecycler(weather: WeatherResponse? = null) {
        return when (weather != null) {
            true -> {
                weatherAdapter.setWeather(weather)
            }
            false -> {
                Toast.makeText(activity, "empty data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

