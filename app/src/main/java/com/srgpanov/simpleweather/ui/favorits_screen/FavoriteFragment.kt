package com.srgpanov.simpleweather.ui.favorits_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.FavoriteCurrentItemBinding
import com.srgpanov.simpleweather.databinding.FavoriteLocationItemBinding
import com.srgpanov.simpleweather.databinding.FragmentFavoriteBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.favorites.CurrentViewItem
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment.Companion.KEY_REQUEST_PLACE
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment
import com.srgpanov.simpleweather.ui.weather_screen.DetailFragment.Companion.KEY_FAVORITE_PLACE_SELECTED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
class FavoriteFragment : Fragment(), FragmentResultListener {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var viewModel: FavoriteViewModel
    private val favoritesAdapter: FavoritesAdapter by lazy { FavoritesAdapter() }
    private val favoritesHeaderAdapter: FavoritesHeaderAdapter by lazy { FavoritesHeaderAdapter() }
    private val emptyFavoriteAdapter: EmptyFavoriteAdapter by lazy { EmptyFavoriteAdapter() }
    private val mergeAdapter: ConcatAdapter
            by lazy { ConcatAdapter(favoritesHeaderAdapter, favoritesAdapter) }

    private var mainActivity: MainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectFavoriteFragment(this)
        shareViewModel = ViewModelProvider(requireActivity())[ShareViewModel::class.java]
        viewModel = ViewModelProvider(this, viewModelFactory)[FavoriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as? MainActivity
        setupInsets()
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        requireActivity()
            .supportFragmentManager
            .setFragmentResultListener(KEY_REQUEST_PLACE, this, this)
    }


    private fun observeViewModel() {
        viewModel.favoritePlaces.observe(viewLifecycleOwner) { places ->
            lifecycleScope.launch {
                favoritesAdapter.setData(places)
                if (places.isEmpty()) {
                    mergeAdapter.addAdapter(emptyFavoriteAdapter)
                } else {
                    mergeAdapter.removeAdapter(emptyFavoriteAdapter)
                }
            }
        }

        viewModel.currentPlace.observe(viewLifecycleOwner) { viewItem: CurrentViewItem? ->
            favoritesHeaderAdapter.current = viewItem
        }
        shareViewModel.refreshWeather.observe(viewLifecycleOwner) {
            viewModel.refreshWeatherFromDetail()
        }
    }


    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity?.navigateToDetailFragment()
            R.id.app_bar_search -> openSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == KEY_REQUEST_PLACE) {
            val place =
                result.getParcelable<PlaceViewItem>(KEY_REQUEST_PLACE)
            checkNotNull(place) { "place null after select on SelectPlaceFragment" }
            onSelectPlace(place)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = requireContext().getString(R.string.favorite)
        binding.toolbar.inflateMenu(R.menu.favorites_menu)
        binding.toolbar.navigationIcon =
            requireContext().getDrawableCompat(R.drawable.ic_arrow_back)
        binding.toolbar.menu.findItem(R.id.app_bar_search).setOnMenuItemClickListener {
            openSearch()
            false
        }
        binding.toolbar.setNavigationOnClickListener {
            Log.d("FavoriteFragment", "setupToolbar: mainActivity = $mainActivity")
            mainActivity?.navigateToDetailFragment()
        }
    }

    private fun setupRecyclerView() {
        setupRvListeners()
        binding.recyclerView.adapter = mergeAdapter
    }

    private fun setupRvListeners() {
        favoritesAdapter.listener = this::bindFavoritesListeners
        favoritesHeaderAdapter.listener = this::bindCurrentListeners
    }

    private fun bindFavoritesListeners(
        itemBinding: FavoriteLocationItemBinding,
        position: Int
    ) {
        itemBinding.constraintLayout.setOnClickListener {
            goToDetailFragment(favoritesAdapter.favorites[position].place)
        }

        itemBinding.optionsIb.setOnClickListener {
            showPopUpMenu(it, favoritesAdapter.favorites[position].place)
        }
    }

    private fun bindCurrentListeners(
        itemBinding: FavoriteCurrentItemBinding,
        item: CurrentViewItem?
    ) {
        itemBinding.constraintLayout.setOnClickListener {
            if (item?.place != null) {
                goToDetailFragment(item.place)
            } else {
                mainActivity?.navigateToDetailFragment()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showPopUpMenu(anchorView: View, place: PlaceViewItem) {
        val menu = FavoriteMenu(anchorView)
        menu.show(requireActivity().windowManager.defaultDisplay)
        menu.onRemoveClick = {
            viewModel.removeFavoritePlace(place)
        }
        menu.onRenameClick = {
            showRenameDialog(place)
        }
    }

    private fun showRenameDialog(placeViewItem: PlaceViewItem) {
        val renameDialog = RenameDialog(placeViewItem.title)
        renameDialog.onOkClick = { text: String ->
            if (placeViewItem.title != text) {
                viewModel.renamePlace(placeViewItem.copy(title = text))
            }
        }
        renameDialog.show(childFragmentManager, null)
    }

    private fun onSelectPlace(placeViewItem: PlaceViewItem) {
        viewModel.savePlaceToHistory(placeViewItem)
        val favoriteOrCurrent = viewModel.placeFavoriteOrCurrent(placeViewItem)
        if (favoriteOrCurrent) {
            goToDetailFragment(placeViewItem)
        } else {
            val detailFragment = DetailFragment.newInstance(placeViewItem, true)
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, detailFragment, detailFragment::class.java.simpleName)
                .addToBackStack(detailFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun goToDetailFragment(placeView: PlaceViewItem) {
        val bundle = bundleOf(KEY_FAVORITE_PLACE_SELECTED to placeView)
        parentFragmentManager
            .setFragmentResult(KEY_FAVORITE_PLACE_SELECTED, bundle)
        mainActivity?.navigateToDetailFragment()
    }


    private fun openSearch() {
        val selectFragment = SelectPlaceFragment()
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out
            )
            .replace(R.id.container, selectFragment, SelectPlaceFragment.TAG)
            .addToBackStack(SelectPlaceFragment.TAG)
            .commit()
    }

    private fun setupInsets() {
        binding.statusBackground.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP)
        binding.recyclerView.addSystemWindowInsetToPadding(bottom = true)
    }

    fun onBackPressed() {
        mainActivity?.navigateToDetailFragment()
    }
}
