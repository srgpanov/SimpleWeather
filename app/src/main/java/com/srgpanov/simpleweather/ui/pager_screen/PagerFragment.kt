package com.srgpanov.simpleweather.ui.pager_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.srgpanov.simpleweather.databinding.PagerFragmentLayoutBinding
import com.srgpanov.simpleweather.other.OnBackPressedListener
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment

class PagerFragment : Fragment(), OnBackPressedListener {
    private var _binding: PagerFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private var pagerAdapter: Pager2Adapter? = null
    private val leftEdgeScrollHelper = LeftEdgeScrollHelper()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PagerFragmentLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = Pager2Adapter(this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setCurrentItem(1, false)
        binding.viewPager.setPageTransformer(PagerTransformer())
        val rv = binding.viewPager[0] as RecyclerView
        leftEdgeScrollHelper.attachToRecyclerView(rv)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val scrollHelperIsEnabled = when (position) {
                    0 -> false
                    1 -> true
                    else -> throw IllegalStateException("Pager have more 2 fragments")
                }
                leftEdgeScrollHelper.switchEnable(scrollHelperIsEnabled)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val detailFragment = pagerAdapter?.getFragment(1) as? DetailFragment
                    detailFragment?.updateRV()
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        binding.viewPager.offscreenPageLimit = 1
    }

    fun showFavoriteFragment() {
        Log.d("PagerFragment", "showFavoriteFragment: ${binding.viewPager.currentItem}")
        binding.viewPager.currentItem = 0
    }

    fun showDetailFragment() {
        Log.d(
            "PagerFragment",
            "showDetailFragment: ${binding.viewPager.currentItem} lifecycle ${lifecycle.currentState}"
        )
        lifecycleScope.launchWhenResumed { binding.viewPager.currentItem = 1 }
    }

    override fun onDestroyView() {
        Log.d("PagerFragment", "onDestroyView: ")
        leftEdgeScrollHelper.detachRecyclerView()
        binding.viewPager.adapter = null
        pagerAdapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() {
        when (binding.viewPager.currentItem) {
            0 -> {
                val favoriteFragment = pagerAdapter?.getFragment(0) as? FavoriteFragment
                Log.d(
                    "PagerFragment",
                    "onBackPressed: pagerAdapter $pagerAdapter favoriteFragment $favoriteFragment "
                )
                favoriteFragment?.onBackPressed()
            }
            1 -> {
                val detailFragment = pagerAdapter?.getFragment(1) as? DetailFragment
                Log.d("PagerFragment", "onBackPressed: $detailFragment")
                detailFragment?.onBackPressed()
            }
            else -> throw IllegalStateException("wrong state pager fragment")
        }
    }
}