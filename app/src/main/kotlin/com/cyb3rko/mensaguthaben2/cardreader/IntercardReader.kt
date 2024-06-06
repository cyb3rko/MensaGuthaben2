/*
 * IntercardReader.java
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
package com.cyb3rko.mensaguthaben2.cardreader

import android.util.Log
import com.codebutler.farebot.Utils
import com.codebutler.farebot.card.desfire.DesfireFileSettings.ValueDesfireFileSettings
import com.codebutler.farebot.card.desfire.DesfireProtocol

class IntercardReader : ICardReader {
    override fun readCard(card: DesfireProtocol): ValueData? {
        val appId = 0x5F8415
        val fileId = 1
        Log.i(TAG, "Selecting app and file")
        val settings = Utils.selectAppFile(card, appId, fileId)
        return if (settings is ValueDesfireFileSettings) {
            Log.i(TAG, "found value file")
            Log.i(TAG, "Reading value")
            val data: Int
            try {
                data = card.readValue(fileId)
                ValueData(data, settings.value)
            } catch (e: Exception) {
                Log.w(TAG, "Exception while trying to read value", e)
                null
            }
        } else {
            Log.i(TAG, "File is not a value file, tag is incompatible.")
            null
        }
    }

    companion object {
        private val TAG = IntercardReader::class.java.name
    }
}