package com.srgpanov.simpleweather.ui.forecast_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.other.CalendarItem
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.databinding.ForecastPagerFragmentBinding
import com.srgpanov.simpleweather.other.*

class ForecastPagerFragment : Fragment() {
    private lateinit var viewModel: ForecastPagerViewModel
    private lateinit var dateAdapter: CalendarAdapter
    private lateinit var forecastAdapter: ForecastPagerAdapter
    private var _binding: ForecastPagerFragmentBinding? = null
    private val binding get() = _binding!!
    private var itemCompletelyVisibleListener: FirstItemCompletelyVisibleListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = arguments?.getInt("position", 0) ?: 0
        val oneCall = arguments?.getParcelable<OneCallResponse>("oneCall")
        val factory = ForecastViewModelFactory(position, oneCall)
        viewModel =ViewModelProvider(this,factory )[ForecastPagerViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ForecastPagerFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareViews()
    }

    private fun prepareViews() {
        setupToolbar()
        setupInsets()
        setupCalendar()
    }

    private fun setupInsets() {
        binding.appbarLayout.addSystemWindowInsetToPadding(top = true)
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

    private fun setupCalendar() {
        dateAdapter = CalendarAdapter()
        binding.calendar.adapter = dateAdapter
        dateAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                dateAdapter.selectDay(position)
                binding.viewPager.setCurrentItem(position, true)
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dateAdapter.selectDay(position)
                viewModel.daySelected = position
            }
        })
        forecastAdapter = ForecastPagerAdapter()
        forecastAdapter.itemVisibleListener = itemCompletelyVisibleListener
        binding.viewPager.adapter = forecastAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewModel.oneCallResponse.observe(viewLifecycleOwner, Observer {
            forecastAdapter.forecasts = it.daily.toMutableList()
            val dateList: MutableList<CalendarItem> = mutableListOf()
            it.daily.forEach { forecast ->
                dateList.add(
                    CalendarItem(
                        forecast.date()
                    )
                )
            }
            dateAdapter.setData(dateList.toList())
            val currentDay = viewModel.daySelected
            binding.viewPager.setCurrentItem(currentDay, false)
            logD("selectDay")
            dateAdapter.selectDay(currentDay)
        })
    }

    override fun onStart() {
        super.onStart()
        logD("lifecycle onStart")
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        itemCompletelyVisibleListener = object : FirstItemCompletelyVisibleListener {
            override fun isVisible(isVisible: Boolean) {
                if (isVisible) {
                    binding.appbarLayout.elevation = 0f
                    binding.statusBackground.elevation = 0f
                } else {
                    if (binding.appbarLayout.elevation == 0f) {
                        binding.appbarLayout.elevation = 8f
                        binding.statusBackground.elevation = 8f
                    }
                }
            }
        }
    }
}
