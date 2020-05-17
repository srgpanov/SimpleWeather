package com.srgpanov.simpleweather.ui.favorits_screen

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.remote.ResponseResult.Failure
import com.srgpanov.simpleweather.data.remote.ResponseResult.Success
import com.srgpanov.simpleweather.databinding.FragmentFavoriteBinding
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment
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
    private val remoteDataSource by lazy { RemoteDataSourceImpl() }
    private val historyAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val favoritesAdapter: FavoritesAdapter by lazy { FavoritesAdapter() }
    private val favoritesHeaderAdapter: FavoritesHeaderAdapter by lazy { FavoritesHeaderAdapter() }
    private val emptyFavoriteAdapter: EmptyFavoriteAdapter by lazy { EmptyFavoriteAdapter() }
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }
    lateinit var mergeAdapter: MergeAdapter
    private var mainActivity: MainActivity? = null
    private var searchJob: Job? = null


    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        mainActivity = requireActivity() as MainActivity
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SelectPlaceFragment.REQUEST_PLACE,
            this,
            FragmentResultListener { requestKey, result ->
                if (requestKey == SelectPlaceFragment.REQUEST_PLACE) {
                    val place = result.getParcelable<PlaceEntity>(SelectPlaceFragment.REQUEST_PLACE)
                    if (place != null) {
                        onSelectPlace(place)
                    }
                }
            })
        logD("lifecycle onCreate  ${this}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        prepareViews()
        logD("lifecycle onCreateView  ${this}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.searchHistory.observe(viewLifecycleOwner, Observer {
            it.forEach { place ->
                logD("searchHistory ${place}")
            }
            historyAdapter.setData(it)
        })
        viewModel.favoritePlaces.observe(viewLifecycleOwner, Observer { places ->
            favoritesAdapter.setData(places)
            if (places.isEmpty()) {
                mergeAdapter.addAdapter(emptyFavoriteAdapter)
            } else {
                mergeAdapter.removeAdapter(emptyFavoriteAdapter)
            }
        })
        viewModel.currentPlace.observe(viewLifecycleOwner, Observer {
            logD("current value $it ")
            favoritesHeaderAdapter.current = it
            favoritesHeaderAdapter.notifyDataSetChanged()
        })
        shareViewModel.currentPlace.observe(viewLifecycleOwner, Observer {
            viewModel.currentPlace.value = it
        })
        shareViewModel.refreshWeather.observe(viewLifecycleOwner, Observer {
            logD("refreshWeather")
            viewModel.refreshWeather()
        })
    }

    override fun onPause() {
        super.onPause()
        logD("lifecycle onPause ${this}")
    }

    override fun onStop() {
        super.onStop()
        logD("lifecycle onStop ${this}")

    }

    override fun onDestroyView() {
        _binding = null
        logD("lifecycle onDestroyView ${this}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshPlaces()
        binding.searchView.isIconified = !viewModel.searchViewOpen
    }

    override fun onResume() {
        super.onResume()
    }

    private fun prepareViews() {
        setupInsets()
        setupToolbar()
    }


    private fun setupRecyclerView() {
        setupRvListeners()
        favoritesAdapter.scope = scope
        mergeAdapter = MergeAdapter(favoritesHeaderAdapter, favoritesAdapter)
        binding.recyclerView.adapter = mergeAdapter
    }
    private fun setupRvListeners(){
        favoritesAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                val place = favoritesAdapter.places[position]
                goToDetailFragment(place)
            }
        }
        favoritesAdapter.optionsListener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                view?.let {
                    showPopUpMenu(it, position)
                }
            }
        }
        favoritesHeaderAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                favoritesHeaderAdapter.current?.let {
                    goToDetailFragment(it)
                }
            }
        }
        historyAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                onSelectPlace(historyAdapter.searchHistoryList[position])
            }
        }
        searchAdapter.listener = object : MyClickListener {
            override fun onClick(view: View?, position: Int) {
                val featureMember = searchAdapter.featureMember[position]
                val place = PlaceEntity(
                    title = featureMember.GeoObject.name,
                    lat = featureMember.GeoObject.Point.getGeoPoint().lat,
                    lon = featureMember.GeoObject.Point.getGeoPoint().lon,
                    cityFullName = featureMember.getFormatedName()
                )
                onSelectPlace(place)
            }
        }
    }

    private fun showPopUpMenu(it: View, position: Int) {
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.favorite_popup_menu_layout, null)
        val menu = PopupWindow(requireContext())
        val menuHeight = dpToPx(96)
        menu.contentView = layout
        menu.height = menuHeight
        menu.width = dpToPx(180)
        menu.isFocusable = true
        menu.elevation = 8F;
        menu.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        menu.animationStyle = android.R.style.Widget_Material_PopupMenu
        val location = IntArray(2)
        it.getLocationOnScreen(location)
        val display: Display = requireActivity().getWindowManager().getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        val displayHeight: Int = size.y
        val menuPinnedTop = (location[1] + it.height + menu.height) > displayHeight
        if (menuPinnedTop) {
            menu.animationStyle = R.style.AnimationPopupTop
        } else {
            menu.animationStyle = R.style.AnimationPopupBottom
        }
        menu.showAsDropDown(it)
        val removeView = layout.findViewById<View>(R.id.menu_remove_tv)
        val renameView = layout.findViewById<View>(R.id.menu_rename_tv)
        removeView.setOnClickListener {
            menu.dismiss()
            removeFavoritePlace(favoritesAdapter.places[position])
        }
        renameView.setOnClickListener {
            menu.dismiss()
            renamePlace(favoritesAdapter.places[position])
        }
    }

    private fun removeFavoritePlace(placeEntity: PlaceEntity) {
        logD("menu ${placeEntity.title}")
        viewModel.removeFavoritePlace(placeEntity)
    }

    private fun renamePlace(placeEntity: PlaceEntity) {
        logD("menu ${placeEntity.title}")
        showAlertDialog(placeEntity)

    }

    private fun showAlertDialog(placeEntity: PlaceEntity) {
        val editText = EditText(requireActivity()).apply {
            setText(placeEntity.title)
            maxLines = 1
        }
        val container = FrameLayout(requireActivity())
        val params: FrameLayout.LayoutParams =
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        params.leftMargin = dpToPx(16)
        params.rightMargin = dpToPx(16)
        params.topMargin = dpToPx(16)
        editText.setLayoutParams(params)
        editText.isSingleLine = true
        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        container.addView(editText)
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.rename)
            .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    logD("DialogInterface ${editText.text}")
                    if (placeEntity.title != editText.text.toString()) {
                        viewModel.renamePlace(placeEntity.copy(title = editText.text.toString()))
                    }
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .setView(container)
            .show()
    }

    private fun onSelectPlace(placeEntity: PlaceEntity) {
        viewModel.searchViewOpen = false
        viewModel.savePlaceToHistory(placeEntity)
//        binding.searchView.isIconified = true
        var favoriteOrCurrent = viewModel.placeFavoriteOrCurrent(placeEntity)
        if (favoriteOrCurrent) {
            goToDetailFragment(placeEntity)
        } else {
            val detailFragment = DetailFragment.newInstance().apply {
                this.arguments = Bundle().apply { putParcelable("place", placeEntity) }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, detailFragment, detailFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun goToDetailFragment(place: PlaceEntity) {
        shareViewModel.weatherPlace.value = place
        mainActivity?.navigateToDetailFragment()
    }


    private fun setupToolbar() {
        binding.backButton.setOnClickListener {
            if (binding.searchView.isIconified) {
                mainActivity?.navigateToDetailFragment()
            } else {
                binding.searchView.isIconified = true

            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setAdapterData(newText.toString())
                return false
            }
        })
        binding.searchView.setOnSearchClickListener {
//            openSearch()
            openSearch2()

        }
        binding.searchView.setOnCloseListener {
            closeSearch()
            false
        }

    }

    private fun openSearch2() {
        val selectFragment = SelectPlaceFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container,selectFragment,SelectPlaceFragment.TAG)
            .addToBackStack(null)
            .commit()
    }

    private fun setAdapterData(query: String) {
        logD("searchview ${binding.searchView.isIconified} searchViewOpen ${viewModel.searchViewOpen} setAdapterData")
        searchJob?.cancel()
        searchJob = scope.launch(Dispatchers.Main) {
            delay(500)
            if (query.length > 1) {
                val placesResponse = remoteDataSource.getPlaces(query = query)
                when (placesResponse) {
                    is Success -> {
                        searchAdapter.setData(placesResponse.data.response.GeoObjectCollection.featureMember)
                        setAdapterToRv(searchAdapter)
                    }
                    is Failure -> logE("searchJob ${placesResponse}")

                }
            } else {
                if (viewModel.searchViewOpen) {
                    setAdapterToRv(historyAdapter)
                }
            }
        }
    }

    private fun setAdapterToRv(adapter: RecyclerView.Adapter<*>) {
        if (binding.recyclerView.adapter != adapter) {
            binding.recyclerView.adapter = adapter
        }
    }


    private fun openSearch() {
        viewModel.searchViewOpen = true
        binding.titleTv.visibility = View.INVISIBLE
        binding.recyclerView.adapter = historyAdapter
    }

    private fun closeSearch() {
        viewModel.searchViewOpen = false
        binding.titleTv.visibility = View.VISIBLE
        binding.recyclerView.adapter = mergeAdapter
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

    fun onBackPressed() {
        logD("back fragment")
        if (binding.searchView.isIconified) {
            mainActivity?.navigateToDetailFragment()
        } else {
            binding.searchView.isIconified = true
            viewModel.searchViewOpen = false
        }
    }
}
