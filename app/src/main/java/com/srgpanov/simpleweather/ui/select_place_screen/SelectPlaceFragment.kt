package com.srgpanov.simpleweather.ui.select_place_screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.srgpanov.simpleweather.App
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.data.remote.ResponseResult.Failure
import com.srgpanov.simpleweather.data.remote.ResponseResult.Success
import com.srgpanov.simpleweather.databinding.SelectPlaceFragmentBinding
import com.srgpanov.simpleweather.domain_logic.view_entities.weather.PlaceViewItem
import com.srgpanov.simpleweather.other.InsetSide
import com.srgpanov.simpleweather.other.addSystemWindowInsetToPadding
import com.srgpanov.simpleweather.other.logE
import com.srgpanov.simpleweather.other.setHeightOrWidthAsSystemWindowInset
import com.srgpanov.simpleweather.ui.favorits_screen.SearchAdapter
import com.srgpanov.simpleweather.ui.favorits_screen.SearchHistoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class SelectPlaceFragment : Fragment() {
    private var _binding: SelectPlaceFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SelectPlaceViewModel
    private var mainActivity: MainActivity? = null
    private val historyAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }

    companion object {
        val TAG = SelectPlaceFragment::class.java.simpleName
        const val KEY_REQUEST_PLACE = "REQUEST_PLACE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectSelectPlaceFragment(this)
        mainActivity = requireActivity() as? MainActivity
        viewModel = ViewModelProvider(this, viewModelFactory)[SelectPlaceViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SelectPlaceFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        setupListeners()
        setupOtherView()
        observeViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observeViewModel() {
        viewModel.searchHistory.observe(viewLifecycleOwner) {
            historyAdapter.setData(it)
        }
    }

    private fun setupListeners() {
        binding.searchView.queryTextChanges()
            .onEach(this::selectAdapter)
            .debounce(500)
            .filter { it.length > 1 }
            .onEach(this::setAdapterData)
            .launchIn(lifecycleScope)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        historyAdapter.listener = { position ->
            onSelectPlace(historyAdapter.searchHistoryList[position])
        }
        searchAdapter.listener = { position ->
            val featureMember = searchAdapter.featureMember[position]
            val place =
                PlaceViewItem(
                    title = featureMember.geoObject.name,
                    lat = featureMember.geoObject.point.toGeoPoint().lat,
                    lon = featureMember.geoObject.point.toGeoPoint().lon,
                    cityFullName = featureMember.getFormattedName()
                )
            onSelectPlace(place)
        }
        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
        }
    }

    private fun setupInsets() {
        binding.statusBackground.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP)
        binding.recyclerView.addSystemWindowInsetToPadding(bottom = true)
    }

    private suspend fun setAdapterData(query: String) {
        when (val placesResponse = viewModel.getPlaces(query = query)) {
            is Success -> searchAdapter.setData(placesResponse.data.response.geoObjectCollection.featureMember)
            is Failure -> logE("searchJob $placesResponse")
        }
    }

    private suspend fun selectAdapter(query: String) = withContext(Dispatchers.Main.immediate) {
        val rvAdapter = if (query.length < 2) historyAdapter else searchAdapter
        if (binding.recyclerView.adapter != rvAdapter) {
            binding.recyclerView.adapter = rvAdapter
        }
    }


    private fun setupOtherView() {
        binding.recyclerView.adapter = historyAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchView.requestFocus()
    }

    private fun onSelectPlace(placeViewItem: PlaceViewItem) {
        val bundle = Bundle()
        bundle.putParcelable(KEY_REQUEST_PLACE, placeViewItem)
        requireActivity().supportFragmentManager.setFragmentResult(KEY_REQUEST_PLACE, bundle)
        parentFragmentManager.popBackStack()
        Log.d(
            "SelectPlaceFragment",
            "onSelectPlace: hashCode ${requireActivity().supportFragmentManager.hashCode()}"
        )
    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun SearchView.queryTextChanges(): Flow<String> = callbackFlow {
        val listener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                offer(newText.toString())
                return false
            }
        }
        setOnQueryTextListener(listener)
        awaitClose { setOnQueryTextListener(null) }
    }

}
