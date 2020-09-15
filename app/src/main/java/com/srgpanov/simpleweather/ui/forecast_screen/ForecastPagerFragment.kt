package com.srgpanov.simpleweather.ui.forecast_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.viewpager2.widget.ViewPager2
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.weather.OneCallResponse
import com.srgpanov.simpleweather.databinding.ForecastPagerFragmentBinding
import com.srgpanov.simpleweather.di.ArgumentsViewModelFactory
import com.srgpanov.simpleweather.other.FirstItemCompletelyVisibleListener
import com.srgpanov.simpleweather.other.InsetSide
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding
import com.srgpanov.simpleweather.other.setHeightOrWidthAsSystemWindowInset
import javax.inject.Inject

class ForecastPagerFragment private constructor() : Fragment() {
    private var _binding: ForecastPagerFragmentBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var viewModel: ForecastViewModel
    private val dateAdapter: CalendarAdapter by lazy { CalendarAdapter() }
    private val forecastAdapter: ForecastPagerAdapter by lazy { ForecastPagerAdapter() }

    @Inject
    lateinit var forecastFactory: ForecastViewModelFactory

    companion object {
        const val ARG_POSITION = "ARG_POSITION"
        const val ARG_ONE_CALL = "ARG_ONE_CALL"

        fun newInstance(response: OneCallResponse, position: Int): ForecastPagerFragment {
            return ForecastPagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                    putParcelable(ARG_ONE_CALL, response)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectForecastFragment(this)
        val assistedFactory = ArgumentsViewModelFactory(forecastFactory, requireArguments())
        viewModel = ViewModelProvider(this, assistedFactory)[ForecastViewModel::class.java]
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
        binding.statusBackground.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP)
    }

    private fun setupCalendar() {
        binding.calendar.adapter = dateAdapter
        dateAdapter.listener = { position ->
            dateAdapter.selectDay(position)
            binding.viewPager.setCurrentItem(position, true)
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dateAdapter.selectDay(position)
                viewModel.daySelected = position
            }
        })
        binding.viewPager.adapter = forecastAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewModel.forecastList.observe(viewLifecycleOwner) { forecasts ->
            forecastAdapter.setData(forecasts)
            binding.viewPager.setCurrentItem(viewModel.daySelected, false)
        }
        viewModel.calendarDay.observe(viewLifecycleOwner) { days ->
            dateAdapter.setData(days)
            dateAdapter.selectDay(viewModel.daySelected)
        }

    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        val itemCompletelyVisibleListener = object : FirstItemCompletelyVisibleListener {
            override fun isVisible(isVisible: Boolean) {
                val elevation: Float = if (isVisible) 0f else 8f

                binding.appbarLayout.elevation = elevation
                binding.statusBackground.elevation = elevation
            }
        }
        forecastAdapter.itemVisibleListener = itemCompletelyVisibleListener
    }
}
