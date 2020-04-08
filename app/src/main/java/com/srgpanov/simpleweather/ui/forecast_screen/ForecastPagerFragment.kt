package com.srgpanov.simpleweather.ui.forecast_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.other.CalendarItem
import com.srgpanov.simpleweather.databinding.ForecastPagerFragmentBinding
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.ShareViewModel

class ForecastPagerFragment : Fragment() {
    private lateinit var viewModel: ForecastPagerViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var dateAdapter: CalendarAdapter
    private lateinit var forecastAdapter: ForecastPagerAdapter
    private var _binding: ForecastPagerFragmentBinding? = null
    private val binding get() = _binding!!
    var itemCompletelyVisibleListener:FirstItemCompletelyVisibleListener?=null

    companion object {
        fun newInstance() = ForecastPagerFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForecastPagerViewModel::class.java)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ForecastPagerFragmentBinding.inflate(layoutInflater, container, false)
        prepareViews()
        return binding.root
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
        forecastAdapter.itemVisibleListener=itemCompletelyVisibleListener
        binding.viewPager.adapter = forecastAdapter
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        if (shareViewModel.daySelected >= 0) {
            logD("daySelected ${shareViewModel.daySelected}")
            viewModel.daySelected = shareViewModel.daySelected
            viewModel.request.value = shareViewModel.request
            shareViewModel.daySelected = -1
        }
        val request = viewModel.request.value
        request?.let {
            forecastAdapter.forecasts = it.forecasts.toMutableList()
            val dateList: MutableList<CalendarItem> = mutableListOf()
            it.forecasts.forEach { forecast ->
                dateList.add(
                    CalendarItem(
                        forecast.getDate()
                    )
                )
            }
            dateAdapter.setData(dateList.toList())
        }
        val currentDay = viewModel.daySelected
        binding.viewPager.setCurrentItem(currentDay, false)
        dateAdapter.selectDay(currentDay)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        itemCompletelyVisibleListener= object : FirstItemCompletelyVisibleListener {
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
    }}
