package com.srgpanov.simpleweather.ui.about_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.srgpanov.simpleweather.databinding.AboutFragmentBinding
import com.srgpanov.simpleweather.other.InsetSide
import com.srgpanov.simpleweather.other.setHeightOrWidthAsSystemWindowInset

class AboutFragment : Fragment() {
    private var _binding: AboutFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AboutFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        setupToolbar()
    }

    private fun setupInsets() {
        binding.statusBackground.setHeightOrWidthAsSystemWindowInset(InsetSide.TOP)

    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

}