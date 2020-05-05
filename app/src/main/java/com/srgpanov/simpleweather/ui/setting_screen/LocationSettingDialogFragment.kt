package com.srgpanov.simpleweather.ui.setting_screen

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.srgpanov.simpleweather.MainActivity
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.ui.ShareViewModel
import com.srgpanov.simpleweather.ui.select_place_screen.SelectPlaceFragment

class LocationSettingDialogFragment:DialogFragment() {
    private var mainActivity: MainActivity?=null
    private lateinit var shareViewModel: ShareViewModel
    var onLocationTypeChoiceCallback:OnLocationTypeChoiceCallback?=null
    companion object{
        val TAG = this::class.java.simpleName
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        mainActivity=requireActivity() as? MainActivity
        val fragmentView =
            requireActivity().layoutInflater.inflate(R.layout.location_setting_dialog_fragment, null)
        val currentTV = fragmentView.findViewById<TextView>(R.id.current_location_tv)
        val otherTv = fragmentView.findViewById<TextView>(R.id.other_tv)
        otherTv.setOnClickListener {
            mainActivity?.navigate(SelectPlaceFragment::class.java)
            dismiss()
        }
        currentTV.setOnClickListener {
            shareViewModel = ViewModelProvider(requireActivity()).get(ShareViewModel::class.java)
            shareViewModel.savePreferences(true)
            onLocationTypeChoiceCallback?.onLocationTypeChoice()
            dismiss()
        }
        return AlertDialog.Builder(requireActivity()).setView(fragmentView).create()
    }
    interface OnLocationTypeChoiceCallback{
        fun onLocationTypeChoice()
    }
}