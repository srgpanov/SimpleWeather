package com.srgpanov.simpleweather

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.other.NavigationActivity
import com.srgpanov.simpleweather.other.OnBackPressedListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.pager_screen.PagerFragment


class MainActivity : AppCompatActivity(), NavigationActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PagerFragment(), PagerFragment::class.java.simpleName)
                .commit()
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }


    fun navigateToFavoriteFragment(){
        val pagerFragment = supportFragmentManager.findFragmentByTag(PagerFragment::class.java.simpleName) as? PagerFragment
        logD("PagerFragment $pagerFragment  TAG ${PagerFragment::class.java.simpleName}")
        pagerFragment?.showFavoriteFragment()
    }
    fun navigateToDetailFragment(){
        val pagerFragment = supportFragmentManager.findFragmentByTag(PagerFragment::class.java.simpleName) as? PagerFragment
        logD("PagerFragment $pagerFragment  TAG ${PagerFragment::class.java.simpleName}")
        pagerFragment?.showDetailFragment()
    }

    override fun onBackPressed() {
        logD("MainActivity onBackPressed")
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is OnBackPressedListener) {
                fragment.also { Log.d("MainActivity", "onBackPressed: $it") }.onBackPressed()
                Log.d("MainActivity", "onBackPressed: back OnBackPressedListener")
            } else {
                Log.d("MainActivity", "onBackPressed: back super")
                super.onBackPressed()
            }
        }
    }
    fun onBackPressedSuper(){
        Log.d("MainActivity", "onBackPressedSuper: ")
        super.onBackPressed()
    }

    override fun navigateToFragment(fragment: Fragment) {
        val tag=fragment::class.java.simpleName
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }
}
