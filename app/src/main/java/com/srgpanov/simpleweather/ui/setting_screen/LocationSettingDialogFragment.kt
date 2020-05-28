package com.srgpanov.simpleweather.ui.setting_screen

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.srgpanov.simpleweather.R

class LocationSettingDialogFragment:DialogFragment() {
    var onLocationTypeChoiceCallback:OnLocationTypeChoiceCallback?=null
    companion object{
        val TAG = this::class.java.simpleName
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fragmentView =
            requireActivity().layoutInflater.inflate(R.layout.location_setting_dialog_fragment, null)
        val currentTV = fragmentView.findViewById<TextView>(R.id.current_location_tv)
        val otherTv = fragmentView.findViewById<TextView>(R.id.other_tv)
        otherTv.setOnClickListener {
            onLocationTypeChoiceCallback?.onLocationTypeChoice(LocationType.CERTAIN)
            dismiss()
        }
        currentTV.setOnClickListener {
            onLocationTypeChoiceCallback?.onLocationTypeChoice(LocationType.CURRENT)
            dismiss()
        }
        return AlertDialog.Builder(requireActivity()).setView(fragmentView).create()
    }
    interface OnLocationTypeChoiceCallback{
        fun onLocationTypeChoice(type:LocationType)
    }
    enum class LocationType{
        CURRENT,CERTAIN
    }
}