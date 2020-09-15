package com.srgpanov.simpleweather.ui.favorits_screen

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.srgpanov.simpleweather.R
import com.srgpanov.simpleweather.databinding.FavoriteRenameDialogBinding

class RenameDialog(val initText: String) : DialogFragment() {
    var onOkClick: ((String) -> Unit)? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FavoriteRenameDialogBinding.inflate(requireActivity().layoutInflater)
        requireActivity().layoutInflater.inflate(R.layout.location_setting_dialog_fragment, null)
        binding.etRenameDialog.setText(initText)
        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.rename)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val text = binding.etRenameDialog.text.toString()
                onOkClick?.invoke(text)
            }
            .setView(binding.root.also { Log.d("RenameDialog", "onCreateDialog: $it") })
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }
}