package com.srgpanov.simpleweather.ui.pager_screen

import androidx.collection.LongSparseArray
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.srgpanov.simpleweather.ui.favorits_screen.FavoriteFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment

class Pager2Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoriteFragment()
            1 -> DetailFragment.newInstance()
            else -> throw IllegalStateException("wrong fragment")
        }
    }


    fun getFragment(position: Int): Fragment {
        val field = this.javaClass.superclass!!.getDeclaredField("mFragments")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val fragments: LongSparseArray<Fragment> = field.get(this) as LongSparseArray<Fragment>
        return fragments.valueAt(position)
    }

}