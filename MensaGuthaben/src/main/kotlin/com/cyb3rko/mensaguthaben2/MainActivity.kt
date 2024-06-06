@file: OptIn(ExperimentalMaterial3Api::class)

package com.cyb3rko.mensaguthaben2

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.codebutler.farebot.card.desfire.DesfireException
import com.cyb3rko.mensaguthaben2.cardreader.Readers
import com.cyb3rko.mensaguthaben2.cardreader.ValueData
import com.cyb3rko.mensaguthaben2.modals.NfcOffDialog
import com.cyb3rko.mensaguthaben2.ui.theme.MensaGuthabenTheme

internal class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels(
        factoryProducer = { MainViewModel.Factory },
        extrasProducer = {
            MutableCreationExtras(defaultViewModelCreationExtras).apply {
                set(DEFAULT_ARGS_KEY, bundleOf("nfcActivated" to mAdapter.isEnabled))
            }
        }
    )
    private lateinit var mAdapter: NfcAdapter
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                viewModel.showNfcDialog(!mAdapter.isEnabled)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "Compose activity started")
        installSplashScreen()
        super.onCreate(savedInstanceState)

        mAdapter = NfcAdapter.getDefaultAdapter(this)
        @Suppress("DEPRECATION")
        val valueData = intent.getSerializableExtra("valueData") as ValueData?
        if (valueData != null) viewModel.updateValueData(valueData)
        AutostartRegister.register(packageManager, viewModel.uiState.value.autoStart)
        val mIntentFilter = IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED")
        // Create a generic PendingIntent that will be deliver to this activity.
        // The NFC stack will fill in the intent with the details of the discovered tag before
        // delivering to this activity.
        val mPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        // Setup an intent filter
        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        try {
            tech.addDataType("text/plain")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            Log.e(TAG, "Wrong mime")
        }
        val mFilters = arrayOf(tech)
        val mTechLists = arrayOf(
            arrayOf(
                IsoDep::class.java.name,
                NfcA::class.java.name
            )
        )
        applicationContext.registerReceiver(mReceiver, mIntentFilter)
        enableEdgeToEdge()

        setContent {
            LifecycleListener {
                when (it) {
                    Lifecycle.Event.ON_RESUME -> {
                        Log.i(TAG, "Activate foreground dispatch")
                        mAdapter.enableForegroundDispatch(
                            this,
                            mPendingIntent,
                            mFilters,
                            mTechLists
                        )
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        Log.i(TAG, "Deactivate foreground dispatch")
                        mAdapter.disableForegroundDispatch(this)
                    }
                    else -> {}
                }
            }
            MensaGuthabenTheme {
                val uiState by viewModel.uiState.collectAsState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar() }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row {
                            Checkbox(
                                checked = uiState.autoStart,
                                onCheckedChange = {
                                    viewModel.toggleAutoStart()
                                    @Suppress("DEPRECATION")
                                    android.preference.PreferenceManager
                                        .getDefaultSharedPreferences(application)
                                        .edit()
                                        .putBoolean("autostart", it)
                                        .apply()
                                }
                            )
                            Text(
                                modifier = Modifier.padding(top = 15.dp),
                                text = "Autostart on tag discovery",
                                style = TextStyle(fontSize = 16.sp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                        ValueContent(valueData = uiState.valueData)
                    }
                }
                if (uiState.showNfcDialog) {
                    NfcOffDialog(onClose = {
                        viewModel.showNfcDialog(false)
                    })
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i(TAG, "Foreground dispatch")
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Discovered tag with intent: $intent")
            @Suppress("DEPRECATION")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            try {
                val valueData = Readers.instance?.readTag(tag)
                if (valueData != null) {
                    Log.i(TAG, "Setting read data")
                    viewModel.updateValueData(valueData)
                }
            } catch (e: DesfireException) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val TAG: String = MainActivity::class.java.name
    }
}

@Composable
private fun TopBar() {
    TopAppBar(
        title = { Text(stringResource(id = R.string.full_app_name)) }
    )
}
