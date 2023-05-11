package com.cyb3rko.mensaguthaben2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment

/**
 * Created by wenzel on 28.11.14.
 */
internal class NfcOffFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.nfc_off)
            .setMessage(R.string.turn_nfc_on)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(R.string.goto_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                requireActivity().startActivity(intent)
            }.create()
    }

    companion object {
        const val TAG = "NfcOffFragment"
    }
}
