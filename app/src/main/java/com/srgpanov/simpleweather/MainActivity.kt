package com.srgpanov.simpleweather

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.other.OnBackPressedListener
import com.srgpanov.simpleweather.other.logD
import com.srgpanov.simpleweather.ui.pager_screen.PagerFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment


class MainActivity : AppCompatActivity() {
    var pagerFragment: PagerFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PagerFragment.newInstance(),PagerFragment::class.java.simpleName)
                .commitNow()
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

    }


    fun navigate(
        fragment: Class<out Fragment>,
        bundle: Bundle? = null,
        addToBackStack: Boolean = true
    ) {
        val instance = fragment.newInstance().apply {
            bundle?.let {
                this.arguments = it
            }
        }
        return when (addToBackStack) {
            true -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, instance, fragment.simpleName)
                    .addToBackStack(fragment.simpleName)
                    .commit()
                Unit
            }
            false -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, instance, fragment.simpleName)
                    .commit()
                Unit
            }
        }

        //todo Доделать навигацию
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
}
