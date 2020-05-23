package com.srgpanov.simpleweather.ui.favorits_screen

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.MergeAdapter
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
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
    private val favoritesAdapter: FavoritesAdapter by lazy { FavoritesAdapter() }
    private val favoritesHeaderAdapter: FavoritesHeaderAdapter by lazy { FavoritesHeaderAdapter() }
    private val emptyFavoriteAdapter: EmptyFavoriteAdapter by lazy { EmptyFavoriteAdapter() }
    private lateinit var mergeAdapter: MergeAdapter
    private var mainActivity: MainActivity? = null


    companion object {
        @JvmStatic
        fun newInstance() =
            FavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
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

        logD("lifecycle onCreateView  ${this}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        setupInsets()
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        viewModel.refreshPlaces()
    }

    private fun observeViewModel() {
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
        binding.recyclerView.adapter=null
        _binding=null
        logD("lifecycle onDestroyView ${this}")
        super.onDestroyView()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> mainActivity?.navigateToDetailFragment()
            R.id.app_bar_search -> openSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        binding.toolbar.title = requireContext().getString(R.string.favorite)
        binding.toolbar.inflateMenu(R.menu.favorites_menu)
        binding.toolbar.navigationIcon = requireContext().getDrawable(R.drawable.ic_arrow_back)
        binding.toolbar.menu.findItem(R.id.app_bar_search).setOnMenuItemClickListener {
            openSearch()
            false
        }
        binding.toolbar.setNavigationOnClickListener {
            mainActivity?.navigateToDetailFragment()
        }

    }

    private fun setupRecyclerView() {
        setupRvListeners()
        favoritesAdapter.scope = scope
        mergeAdapter = MergeAdapter(favoritesHeaderAdapter, favoritesAdapter)
        binding.recyclerView.adapter = mergeAdapter
    }

    private fun setupRvListeners() {
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
        logD("onSelectPlace ${placeEntity.title}")
         viewModel.savePlaceToHistory(placeEntity)
        val favoriteOrCurrent = viewModel.placeFavoriteOrCurrent(placeEntity)
        if (favoriteOrCurrent) {
            goToDetailFragment(placeEntity)
        } else {
            val detailFragment = DetailFragment.newInstance().apply {
                this.arguments = Bundle().apply { putParcelable("place", placeEntity) }
            }
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, detailFragment, detailFragment::class.java.simpleName)
                    .addToBackStack(detailFragment::class.java.simpleName)
                    .commit()
        }
    }

    private fun goToDetailFragment(place: PlaceEntity) {
        shareViewModel.weatherPlace.value = place
        mainActivity?.navigateToDetailFragment()
    }


    private fun openSearch() {
        val selectFragment = SelectPlaceFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.container, selectFragment, SelectPlaceFragment.TAG)
            .addToBackStack(null)
            .commit()
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
        mainActivity?.navigateToDetailFragment()
    }
}
