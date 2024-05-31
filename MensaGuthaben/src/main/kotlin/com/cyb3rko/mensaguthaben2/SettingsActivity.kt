/*
 * SettingsActivity.java
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

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    @Deprecated("Temporary")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the root container of the preferences list
        val root = findViewById<View>(android.R.id.list).parent.parent.parent as LinearLayout
        val bar =
            LayoutInflater.from(this).inflate(R.layout.preferences_toolbar, root, false) as Toolbar
        root.addView(bar, 0) // insert at top
        bar.setTitle(R.string.title_activity_settings)
        bar.setNavigationOnClickListener { v: View? -> finish() }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupSimplePreferencesScreen()
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private fun setupSimplePreferencesScreen() {
        if (isNotSimplePreferences(this)) {
            return
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general)
    }

    @Deprecated("Temporary")
    /** {@inheritDoc}  */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this) && isNotSimplePreferences(this)
    }

    @Deprecated("Temporary")
    /** {@inheritDoc}  */
    override fun onBuildHeaders(target: List<Header>) {
        if (isNotSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target)
        }
    }

    /**
     * Settings changed, so register or unregister nfc listener
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null && key == "autostart") {
            val value = sharedPreferences.getBoolean(key, true)
            AutostartRegister.register(packageManager, value)
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class GeneralPreferenceFragment : PreferenceFragment() {
        @Deprecated("Temporary")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
        }
    }

    companion object {
        /**
         * Determines whether to always show the simplified settings UI, where
         * settings are presented in a single list. When false, settings are shown
         * as a master/detail two-pane view on tablets. When true, a single pane is
         * shown on tablets.
         */
        private const val ALWAYS_SIMPLE_PREFS = false

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return (context.resources.configuration.screenLayout
                    and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Determines whether the simplified settings UI should be shown. This is
         * true if this is forced via [.ALWAYS_SIMPLE_PREFS], or the device
         * doesn't have newer APIs like [PreferenceFragment], or the device
         * doesn't have an extra-large screen. In these cases, a single-pane
         * "simplified" settings UI should be shown.
         */
        private fun isNotSimplePreferences(context: Context): Boolean {
            return !ALWAYS_SIMPLE_PREFS && isXLargeTablet(context)
        }
    }
}