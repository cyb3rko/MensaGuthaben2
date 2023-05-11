package com.cyb3rko.mensaguthaben2

import android.app.ActivityOptions
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.codebutler.farebot.card.desfire.DesfireException
import com.cyb3rko.mensaguthaben2.cardreader.Readers

/**
 * Created by wenzel on 28.11.14.
 */
class PopupActivity : AppCompatActivity() {
    private lateinit var valueFragment: ValueFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        Log.i(TAG, "activity started")
        valueFragment = ValueFragment()
        supportFragmentManager.beginTransaction().replace(
            R.id.main,
            valueFragment,
            VALUE_TAG
        ).commit()
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Started by tag discovery")
            onNewIntent(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, R.id.fullscreen, Menu.NONE, R.string.fullscreen)
            .setIcon(R.drawable.ic_action_full_screen).setShowAsAction(
            MenuItem.SHOW_AS_ACTION_ALWAYS
        )
        return true
    }

    public override fun onStart() {
        super.onStart()
        ValueHolder.data?.let {
            valueFragment.valueData = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.fullscreen) {
            val intent = Intent(this@PopupActivity, MainActivity::class.java)
            intent.action = MainActivity.ACTION_FULLSCREEN
            intent.putExtra(MainActivity.EXTRA_VALUE, valueFragment.valueData)
            animateActivity(intent)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun animateActivity(intent: Intent) {
        //TODO check what happens if last is empty
        val activityOptions = if (valueFragment.valueData != null) {
            ActivityOptions.makeSceneTransitionAnimation(
                this@PopupActivity,
                Pair.create(
                    findViewById(
                        R.id.current
                    ), "current"
                ),
                Pair.create(
                    findViewById(
                        R.id.last
                    ), "last"
                ),
                Pair.create(
                    findViewById(
                        R.id.toolbar
                    ), "toolbar"
                )
            )
        } else {
            ActivityOptions.makeSceneTransitionAnimation(
                this@PopupActivity,
                Pair.create(
                    findViewById(
                        R.id.current
                    ), "current"
                ),
                Pair.create(
                    findViewById(
                        R.id.toolbar
                    ), "toolbar"
                )
            )
        }
        startActivity(intent, activityOptions.toBundle())
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            Log.i(TAG, "Discovered tag with intent: $intent")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            try {
                val valueData = Readers.instance?.readTag(tag)
                valueFragment.valueData = valueData
            } catch (e: DesfireException) {
                Toast.makeText(this, R.string.communication_fail, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val TAG = PopupActivity::class.java.simpleName
        private const val VALUE_TAG = "value"
    }
}
