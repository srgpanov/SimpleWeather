package com.srgpanov.simpleweather.ui.favorits_screen

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavoriteFragmentTest {
    var place = PlaceViewItem(
        title = "London",
        lat = 55.0,
        lon = 65.0,
        cityFullName = null,
        favorite = false,
        current = false,
        simpleWeather = null,
        oneCallResponse = null
    )

    @Rule
    @JvmField
    var rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun receiveInputValute() {
        val fragmentScenario = FragmentScenario.launchInContainer(DetailFragment::class.java)


        fragmentScenario.onFragment { fragment ->
            fragment.parentFragmentManager.setFragmentResultListener(
                DetailFragment.KEY_FAVORITE_PLACE_SELECTED,
                fragment
            ) { key, result ->
                place =
                    result.getParcelable<PlaceViewItem>(DetailFragment.KEY_FAVORITE_PLACE_SELECTED)!!
            }
        }

//        onView(withId(R.id.tv_input_code)).check(matches(withText("TMT")))
//        onView(withId(R.id.btn_select_input_currency)).check(matches(withDrawable(R.drawable.ic_tmt)))
        onView(withId(R.id.toolbar)).check(matches(withText("London")))
    }
}