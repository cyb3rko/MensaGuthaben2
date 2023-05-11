/*
 * AboutActivity.java
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

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import de.yazo_games.mensaguthaben.BuildConfig
import de.yazo_games.mensaguthaben.R

class AboutActivity : AppCompatActivity() {
    private fun makeLinkClickable(id: Int) {
        val tv = findViewById<TextView>(id)
        tv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showVersion() {
        try {
            val tv = findViewById<TextView>(R.id.tvVersion)
            tv.text = getString(
                R.string.version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR)
        setContentView(R.layout.activity_about)
        setSupportActionBar(findViewById(R.id.toolbar))
        // Show the Up button in the action bar.
        setupActionBar()
        showVersion()
        makeLinkClickable(R.id.tvCopyright)
        makeLinkClickable(R.id.tvFarebot)
        makeLinkClickable(R.id.tvSource)
        makeLinkClickable(R.id.tvWebsite)
    }

    /**
     * Set up the [android.app.ActionBar].
     */
    private fun setupActionBar() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.about, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}