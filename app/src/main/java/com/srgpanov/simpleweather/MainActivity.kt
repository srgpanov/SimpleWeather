package com.srgpanov.simpleweather

import android.os.Bundle
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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    }

    fun navigateToFavoriteFragment(){
        val pagerFragment = supportFragmentManager.findFragmentByTag(PagerFragment::class.java.simpleName) as? PagerFragment
        logD("pagerFragment $pagerFragment  TAG ${PagerFragment::class.java.simpleName}")
        pagerFragment?.showFavoriteFragment()
    }
    fun navigateToDetailFragment(){
        val pagerFragment = supportFragmentManager.findFragmentByTag(PagerFragment::class.java.simpleName) as? PagerFragment
        logD("pagerFragment $pagerFragment  TAG ${PagerFragment::class.java.simpleName}")
        pagerFragment?.showDetailFragment()
    }



    override fun onBackPressed() {
        logD("MainActivity onBackPressed")
        supportFragmentManager.fragments.forEach {
            if (it is OnBackPressedListener){
                it.onBackPressed()
            }else{
                super.onBackPressed()
            }
        }
    }
    fun onBackPressedSuper(){
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
