package com.srgpanov.simpleweather.ui.favorits_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.RemoteDataSource
import com.srgpanov.simpleweather.data.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.entity.GeoPoint
import com.srgpanov.simpleweather.data.entity.PlaceEntity
import com.srgpanov.simpleweather.databinding.FragmentFavoriteBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var shareViewModel: ShareViewModel
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val remoteDataSource: RemoteDataSource by lazy{RemoteDataSourceImpl()}
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }

    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel= ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        prepareViews()
        return binding.root
    }

    private fun prepareViews() {
        setupInsets()
        setupToolbar()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter=searchAdapter
        searchAdapter.listener= object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                val featureMember = searchAdapter.featureMember[position]
                shareViewModel.featureMember.value=featureMember
                val geoPoint=featureMember.GeoObject.Point.getGeoPoint()
                val placeEntity = PlaceEntity(
                    cityTitle = featureMember.GeoObject.name,
                    geoPoint = geoPoint,
                    cityFullName = featureMember.GeoObject.description,
                    isCurrent = 0

                )
                shareViewModel.savePlace(placeEntity)
                val activity =requireActivity() as MainActivity
                activity.navigate(DetailFragment::class.java)
            }
        }
    }



    private fun setupToolbar() {
        binding.toolbar.run {
            title = "Favorites"
            setNavigationIcon(R.drawable.ic_arrow_back);
            inflateMenu(R.menu.favorites_menu)
            setNavigationOnClickListener { activity?.onBackPressed() }
            val searchItem = menu.findItem(R.id.app_bar_search)
            val searchView = MenuItemCompat.getActionView(searchItem) as? SearchView
            searchView?.let {
                trimChildMargins(searchView)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        scope.launch(Dispatchers.IO) {
                            if (query != null && !query.isEmpty()) {
                                val placesResponse = remoteDataSource.getPlaces(query = query)
                                if (placesResponse != null) {
                                    withContext(Dispatchers.Main){
                                        searchAdapter.setData(placesResponse.response.GeoObjectCollection.featureMember)
                                    }
                                }
                            }
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }
                })
            }
        }
    }

    private fun trimChildMargins(vg: ViewGroup) {
        val childCount = vg.childCount
        for (i in 0 until childCount) {
            val child = vg.getChildAt(i)
            if (child is ViewGroup) {
                trimChildMargins(child)
            }
            val lp = child.layoutParams
            if (lp is MarginLayoutParams) {
                lp.leftMargin = 0
            }
            child.background = null
            child.setPadding(0, 0, 0, 0)
        }
    }

    private fun setupInsets() {
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
}
