package com.srgpanov.simpleweather.ui.weather_screen

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.srgpanov.simpleweather.R

class PermissionNotGrantedDialogFragment : DialogFragment() {
    var onClickListener: DialogInterface.OnClickListener?=null
    companion object{
        val TAG = this::class.java.simpleName
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fragmentView =
            requireActivity().layoutInflater.inflate(R.layout.message_dialog_fragment, null)
        val messageTv = fragmentView.findViewById<TextView>(R.id.dialog_message_tv)
        messageTv.text=getString(R.string.permission_not_granted_message)

        return AlertDialog.Builder(requireActivity()).setView(fragmentView).setPositiveButton(
            getString(R.string.permission),onClickListener
        ).setNegativeButton(getString(R.string.select_place),onClickListener)
            .setCancelable(false)
            .create()
    }

}