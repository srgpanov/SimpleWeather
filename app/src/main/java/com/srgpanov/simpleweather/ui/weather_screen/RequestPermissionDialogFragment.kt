package com.srgpanov.simpleweather.ui.weather_screen

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.srgpanov.simpleweather.R

class RequestPermissionDialogFragment : DialogFragment() {
    var onClickListener: DialogInterface.OnClickListener? = null

    companion object {
        val TAG = this::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fragmentView =
            requireActivity().layoutInflater.inflate(R.layout.message_dialog_fragment, null)

        return AlertDialog.Builder(requireActivity()).setView(fragmentView).setPositiveButton(
            getString(R.string._continue), onClickListener
        ).setCancelable(false)
            .create()
    }

}