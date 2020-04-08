package com.srgpanov.simpleweather.ui.favorits_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.data.DataRepositoryImpl
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.remote.RemoteDataSource
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.databinding.FragmentFavoriteBinding
import com.srgpanov.simpleweather.other.MyClickListener
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding
import com.srgpanov.simpleweather.other.requestApplyInsetsWhenAttached
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var viewModel: FavoriteViewModel
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val remoteDataSource: RemoteDataSource by lazy { RemoteDataSourceImpl() }
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }
    private val favoritesAdapter: FavoritesAdapter by lazy { FavoritesAdapter() }


    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
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
        favoritesAdapter.repository = DataRepositoryImpl(requireActivity())
        favoritesAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                val place = if (position == FavoritesAdapter.CURRENT_POSITION) {
                    viewModel.currentPlace.value
                } else {
                    viewModel.favoritePlaces.value?.get(position)
                }
                place?.let {
                    val activity = requireActivity() as MainActivity
                    activity.navigate(
                        DetailFragment::class.java,
                        Bundle().apply { putParcelable("place", it) },
                        false
                    )
                }
            }
        }
        binding.recyclerView.adapter = favoritesAdapter
        searchAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                onSelectPlace(position)
            }
        }
        viewModel.favoritePlaces.observe(viewLifecycleOwner, Observer {
            favoritesAdapter.setData(it)
            favoritesAdapter.notifyDataSetChanged()
        })
        viewModel.currentPlace.observe(viewLifecycleOwner, Observer {
            favoritesAdapter.cuurent = it
        })
    }

    private fun onSelectPlace(position: Int) {
        val featureMember = searchAdapter.featureMember[position]
        shareViewModel.featureMember.value = featureMember
        val geoPoint = featureMember.GeoObject.Point.getGeoPoint()

        val placeEntity =
            PlaceEntity(
                cityTitle = featureMember.GeoObject.name,
                geoPoint = geoPoint,
                cityFullName = featureMember.GeoObject.description,
                isCurrent = 0
            )
        val activity = requireActivity() as MainActivity
        activity.navigate(
            DetailFragment::class.java,
            Bundle().apply { putParcelable("place", placeEntity) },
            false
        )
    }


    private fun setupToolbar() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                scope.launch(Dispatchers.IO) {
                    if (query != null && query.isNotEmpty()) {
                        val placesResponse = remoteDataSource.getPlaces(query = query)
                        if (placesResponse != null) {
                            withContext(Dispatchers.Main) {
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
        binding.searchView.setOnSearchClickListener {
            openSearch()
        }
        binding.searchView.setOnCloseListener {
            closeSearch()
            false
        }

    }

    private fun openSearch() {
        binding.titleTv.visibility = View.INVISIBLE
        binding.recyclerView.adapter = searchAdapter
    }

    private fun closeSearch() {
        binding.titleTv.visibility = View.VISIBLE
        binding.recyclerView.adapter = favoritesAdapter
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
    binding.recyclerView.addSystemWindowInsetToPadding(bottom = true)
}
}
