@file: OptIn(ExperimentalMaterial3Api::class)

package de.cyb3rko.mensaguthaben2

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.codebutler.farebot.card.desfire.DesfireException
import de.cyb3rko.mensaguthaben2.cardreader.Readers
import de.cyb3rko.mensaguthaben2.cardreader.ValueData
import de.cyb3rko.mensaguthaben2.modals.NfcOffDialog
import de.cyb3rko.mensaguthaben2.navigation.Screen
import de.cyb3rko.mensaguthaben2.ui.theme.MensaGuthabenTheme

internal class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels(
        factoryProducer = { MainViewModel.Factory },
        extrasProducer = {
            MutableCreationExtras(defaultViewModelCreationExtras).apply {
                set(DEFAULT_ARGS_KEY, bundleOf("nfcActivated" to mAdapter.isEnabled))
            }
        }
    )
    private lateinit var mAdapter: NfcAdapter
    private lateinit var navController: NavController
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                mainViewModel.showNfcDialog(!mAdapter.isEnabled)
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
        if (valueData != null) mainViewModel.updateValueData(valueData)
        AutostartRegister.register(packageManager, mainViewModel.uiState.value.autoStart)
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
            navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentScreen = Screen.valueOf(
                backStackEntry?.destination?.route ?: Screen.Main.name
            )
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBar(
                        currentScreen = currentScreen,
                        canNavigateBack = navController.previousBackStackEntry != null
                    ) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController as NavHostController,
                        startDestination = Screen.Main.name
                    ) {
                        composable(route = Screen.Main.name) {
                            Main(innerPadding = innerPadding)
                        }
                        composable(route = Screen.About.name) {
                            About(innerPadding = innerPadding)
                        }
                    }
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
                    mainViewModel.updateValueData(valueData)
                }
            } catch (e: DesfireException) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    private fun TopBar(
        currentScreen: Screen,
        canNavigateBack: Boolean,
    ) {
        TopAppBar(
            title = { Text(stringResource(id = currentScreen.title)) },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            },
            actions = {
                if (currentScreen != Screen.About) {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.About.name)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "About button",
                        )
                    }
                }
            },
        )
    }

    @Composable
    private fun Main(innerPadding: PaddingValues) {
        val uiState by mainViewModel.uiState.collectAsState()
        val context = LocalContext.current
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
                        mainViewModel.toggleAutoStart()
                        @Suppress("DEPRECATION")
                        android.preference.PreferenceManager
                            .getDefaultSharedPreferences(context)
                            .edit()
                            .putBoolean("autostart", it)
                            .apply()
                    }
                )
                Text(
                    modifier = Modifier.padding(top = 15.dp),
                    text = stringResource(id = R.string.pref_title_autostart),
                    style = TextStyle(fontSize = 16.sp)
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            ValueContent(valueData = uiState.valueData)
        }
        if (uiState.showNfcDialog) {
            NfcOffDialog(onClose = {
                mainViewModel.showNfcDialog(false)
            })
        }
    }

    companion object {
        private val TAG: String = MainActivity::class.java.name
    }
}
