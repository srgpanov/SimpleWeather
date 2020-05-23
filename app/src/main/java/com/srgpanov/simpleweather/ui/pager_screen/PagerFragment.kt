package com.srgpanov.simpleweather.ui.pager_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE
import com.srgpanov.simpleweather.databinding.PagerFragmentLayoutBinding
import com.srgpanov.simpleweather.other.OnBackPressedListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment

class PagerFragment : Fragment(), OnBackPressedListener {
    private var _binding: PagerFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: PagerAdapter

    companion object {
        fun newInstance() = PagerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PagerFragmentLayoutBinding.inflate(layoutInflater, container, false)
        pagerAdapter = PagerAdapter(childFragmentManager)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.currentItem = 1
        binding.viewPager.setPageTransformer(false, PagerTransformer())
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener(){
            override fun onPageScrollStateChanged(state: Int) {
                if (state==SCROLL_STATE_IDLE){
                    val detailFragment = pagerAdapter.getRegisteredFragment(1) as? DetailFragment
                    detailFragment?.updateRV()
                }
            }
        })
        logD("onCreateView")
        return binding.root
    }

    fun showFavoriteFragment() {
        binding.viewPager.currentItem = 0
    }

    fun showDetailFragment() {
        binding.viewPager.currentItem = 1
    }

    override fun onDestroyView() {
        binding.viewPager.adapter=null
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() {
        when (binding.viewPager.currentItem) {
            0 -> {
                val favoriteFragment = pagerAdapter.getRegisteredFragment(0) as? FavoriteFragment
                favoriteFragment?.onBackPressed()
            }
            1 -> {
                val detailFragment = pagerAdapter.getRegisteredFragment(1) as? DetailFragment
                detailFragment?.onBackPressed()
            }
            else -> throw IllegalStateException("wrong state pager fragment")
        }
    }

}