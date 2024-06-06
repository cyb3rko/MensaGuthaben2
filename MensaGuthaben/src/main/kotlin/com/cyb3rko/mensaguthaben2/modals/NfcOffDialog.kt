@file:OptIn(ExperimentalMaterial3Api::class)

package com.cyb3rko.mensaguthaben2.modals

import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.cyb3rko.mensaguthaben2.R

@Composable
internal fun NfcOffDialog(onClose: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(stringResource(id = R.string.nfc_off)) },
        text = { Text(stringResource(id = R.string.turn_nfc_on)) },
        confirmButton = {
            TextButton(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                }
            ) {
                Text(stringResource(id = R.string.goto_settings))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onClose
            ) {
                Text(stringResource(id = android.R.string.cancel))
            }
        }
    )
}
