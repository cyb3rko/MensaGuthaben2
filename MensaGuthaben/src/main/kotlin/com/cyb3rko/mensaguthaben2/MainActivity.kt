/*
 * MainActivity.java
 *
 * Copyright (C) 2014 Jakob Wenzel
 *
 * Authors:
 * Jakob Wenzel <jakobwenzel92@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cyb3rko.mensaguthaben2

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentFilter.MalformedMimeTypeException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.preference.PreferenceManager
import com.codebutler.farebot.NfcOffFragment
import com.codebutler.farebot.card.desfire.DesfireException
import com.cyb3rko.mensaguthaben2.cardreader.Readers
import com.cyb3rko.mensaguthaben2.cardreader.ValueData
import de.yazo_games.mensaguthaben.R

class MainActivity : AppCompatActivity() {
    private lateinit var mAdapter: NfcAdapter
    private var mPendingIntent: PendingIntent? = null
    private lateinit var mFilters: Array<IntentFilter>
    private lateinit var mTechLists: Array<Array<String>>
    private var mIntentFilter: IntentFilter? = null
    private var mResumed = false
    private var hasNewData = false
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (NfcAdapter.ACTION_ADAPTER_STATE_CHANGED == action) {
                updateNfcState()
            }
        }
    }
    private lateinit var valueFragment: ValueFragment

    fun updateNfcState() {
        if (!mAdapter.isEnabled && mResumed) {
            val f = NfcOffFragment()
            f.show(supportFragmentManager, NfcOffFragment.TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "activity started")
        valueFragment = ValueFragment()
        supportFragmentManager.beginTransaction().replace(
            R.id.main,
            valueFragment,
            VALUE_TAG
        ).commit()

        if (intent.action == ACTION_FULLSCREEN) {
            val valueData = intent.getSerializableExtra(EXTRA_VALUE) as ValueData
            valueFragment.valueData = valueData
            setResult(0)
        }
        val autostart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("autostart", true)
        AutostartRegister.register(packageManager, autostart)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        ViewCompat.setTransitionName(toolbar, "toolbar")
        mAdapter = NfcAdapter.getDefaultAdapter(this)
        mIntentFilter = IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED")

        // Create a generic PendingIntent that will be deliver to this activity.
        // The NFC stack will fill in the intent with the details of the discovered tag before
        // delivering to this activity.
        mPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(
                this,
                javaClass
            ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
        )

        // Setup an intent filter
        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        try {
            tech.addDataType("text/plain")
        } catch (e: MalformedMimeTypeException) {
            Log.e(TAG, "Wrong mime")
        }
        mFilters = arrayOf(tech)
        mTechLists = arrayOf(
            arrayOf(
                IsoDep::class.java.name,
                NfcA::class.java.name
            )
        )
        if (intent.action == ACTION_FULLSCREEN && !hasNewData) {
            val valueData = intent.getSerializableExtra(EXTRA_VALUE) as ValueData
            Log.w(TAG, "restoring data for fullscreen")
            valueFragment.valueData = valueData
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i(TAG, "Foreground dispatch")
        if (intent != null) {
            if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
                Log.i(TAG, "Discovered tag with intent: $intent")
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                try {
                    val valueData = Readers.instance?.readTag(tag)
                    Log.w(TAG, "Setting read data")
                    valueFragment.valueData = valueData
                    hasNewData = true
                } catch (e: DesfireException) {
                    Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
                }
            } else if (intent.action == (ACTION_FULLSCREEN)) {
                val valueData = getIntent().getSerializableExtra(EXTRA_VALUE) as ValueData
                valueFragment.valueData = valueData
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        mResumed = true
        applicationContext.registerReceiver(mReceiver, mIntentFilter)
        updateNfcState()
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists)
    }

    override fun onPause() {
        super.onPause()
        mResumed = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            val myIntent = Intent(this, AboutActivity::class.java)
            startActivity(myIntent)
            return true
        }
        if (item.itemId == R.id.action_settings) {
            val myIntent = Intent(this, SettingsActivity::class.java)
            startActivity(myIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val VALUE_TAG = "Value Fragment"
        const val EXTRA_VALUE = "valueData"
        const val ACTION_FULLSCREEN = "com.cyb3rko.mensaguthaben2.Fullscreen"
        private val TAG: String = MainActivity::class.java.name
    }
}