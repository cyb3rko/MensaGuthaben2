@file: OptIn(ExperimentalMaterial3Api::class)

package com.cyb3rko.mensaguthaben2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.codebutler.farebot.card.desfire.DesfireException
import com.cyb3rko.mensaguthaben2.cardreader.Readers
import com.cyb3rko.mensaguthaben2.cardreader.ValueData
import com.cyb3rko.mensaguthaben2.ui.theme.MensaGuthabenTheme

internal class PopupActivity : ComponentActivity() {
    private var valueData: ValueData? = null

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "Popup activity started")
        super.onCreate(savedInstanceState)
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            Log.i(TAG, "Started by tag discovery")
            onNewIntent(intent)
        }
        setContent {
            MensaGuthabenTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(max = 250.dp),
                    topBar = { TopBar(data = valueData) }
                ) { innerPadding ->
                    ValueContent(innerPadding, valueData = valueData)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Discovered tag with intent: $intent")
            @Suppress("DEPRECATION")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            try {
                val newData = Readers.instance?.readTag(tag)
                valueData = newData
            } catch (e: DesfireException) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val TAG = PopupActivity::class.java.simpleName
    }
}

@Composable
private fun TopBar(data: ValueData?) {
    val context = LocalContext.current
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(
                onClick = {
                    Intent(context, MainActivity::class.java).apply {
                        putExtra("valueData", data)
                        context.startActivity(this)
                        (context as Activity).finish()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(id = R.string.fullscreen)
                )
            }
        }
    )
}
