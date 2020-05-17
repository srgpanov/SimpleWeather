package com.srgpanov.simpleweather.ui.select_place_screen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.data.models.entity.PlaceEntity
import com.srgpanov.simpleweather.data.remote.RemoteDataSourceImpl
import com.srgpanov.simpleweather.data.remote.ResponseResult.*
import com.srgpanov.simpleweather.databinding.SelectPlaceFragmentBinding
import com.srgpanov.simpleweather.other.*
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.favorits_screen.SearchAdapter
import com.srgpanov.simpleweather.ui.favorits_screen.SearchHistoryAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SelectPlaceFragment : Fragment() {
    private var _binding: SelectPlaceFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var viewModel: SelectPlaceViewModel
    private var mainActivity: MainActivity? = null

    private val remoteDataSource by lazy { RemoteDataSourceImpl() }
    private val historyAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val searchAdapter: SearchAdapter by lazy { SearchAdapter() }
    private var searchJob: Job? = null


    companion object {
        @JvmStatic
        fun newInstance() =
            SelectPlaceFragment()
        const val REQUEST_PLACE="REQUEST_PLACE"
        val TAG=SelectPlaceFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = requireActivity() as? MainActivity
        viewModel = ViewModelProvider(this).get(SelectPlaceViewModel::class.java)
        shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
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

    private fun observeViewModel() {
        viewModel.searchHistory.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            historyAdapter.setData(it)
        })
    }

    private fun setupListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setAdapterData(newText.toString())
                return false
            }
        })
        binding.backButton.setOnClickListener {
            mainActivity?.onBackPressedSuper()
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
        binding.searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
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

    private fun setAdapterData(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            selectAdapter(query)
            delay(500)
            if (query.length > 1) {
                val placesResponse = when (Locale.getDefault().language) {
                    "en" -> remoteDataSource.getPlaces(query = query, lang = "en_US")
                    else -> remoteDataSource.getPlaces(query = query)
                }
                when (placesResponse) {
                    is Success -> {
                        searchAdapter.setData(placesResponse.data.response.GeoObjectCollection.featureMember)
                        if (binding.recyclerView.adapter != searchAdapter) {
                            binding.recyclerView.adapter = searchAdapter
                        }
                    }
                    is Failure-> logE("searchJob ${placesResponse}")

                }
            }
        }
    }

    private fun selectAdapter(query: String) {
        if (query.isNotEmpty()) {
            if (binding.recyclerView.adapter != searchAdapter) {
                binding.recyclerView.adapter = searchAdapter
            }
        } else {
            if (binding.recyclerView.adapter != historyAdapter) {
                binding.recyclerView.adapter = historyAdapter
            }
        }
    }


    private fun setupOtherView() {
        binding.recyclerView.adapter = historyAdapter
        binding.searchView.requestFocus()
        binding.searchView
    }

    private fun onSelectPlace(placeEntity: PlaceEntity) {
        val bundle = Bundle()
        bundle.putParcelable(REQUEST_PLACE,placeEntity)
        parentFragmentManager.setFragmentResult(REQUEST_PLACE,bundle)
        parentFragmentManager.popBackStack()

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
}
