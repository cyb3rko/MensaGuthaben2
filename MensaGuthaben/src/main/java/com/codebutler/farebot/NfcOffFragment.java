package com.codebutler.farebot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.DialogFragment;
import de.yazo_games.mensaguthaben.R;

/**
 * Created by wenzel on 28.11.14.
 */
public class NfcOffFragment extends DialogFragment {
	public static final String TAG = "NfcOffFragment";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.nfc_off)
			.setMessage(R.string.turn_nfc_on)
			.setCancelable(true)
			.setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss())
			.setNeutralButton(R.string.goto_settings, (dialog, id) -> {
				Intent intent;
				intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				getActivity().startActivity(intent);
			}).create();
	}
}
