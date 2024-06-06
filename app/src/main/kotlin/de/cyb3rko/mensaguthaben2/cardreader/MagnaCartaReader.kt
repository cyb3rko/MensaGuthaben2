/*
 * MagnaCartaReader.java
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
package de.cyb3rko.mensaguthaben2.cardreader

import android.util.Log
import com.codebutler.farebot.card.desfire.DesfireException
import com.codebutler.farebot.card.desfire.DesfireProtocol

internal class MagnaCartaReader : ICardReader {
    override fun readCard(card: DesfireProtocol): ValueData? {
        val appId = 0xF080F3
        val fileId = 2

        //We don't want to use getFileSettings as they are doing some weird stuff with the fileType
        return try {
            card.selectApp(appId)

            //For some reason we can't use getFileList either, because the card answers with an
            //authentication error
            val data = card.readFile(fileId)
            val low = data[7].toInt() and 0xFF
            val hi = data[6].toInt() and 0xFF
            val value = hi shl 8 or low
            ValueData(value * 10, null)
        } catch (e: DesfireException) {
            Log.w(TAG, "Exception while reading tag")
            null
        }
    }

    companion object {
        private val TAG = MagnaCartaReader::class.java.name
    }
}
