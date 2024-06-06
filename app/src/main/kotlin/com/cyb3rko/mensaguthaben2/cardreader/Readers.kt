/*
 * Readers.java
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

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import com.codebutler.farebot.card.desfire.DesfireException
import com.codebutler.farebot.card.desfire.DesfireProtocol
import com.cyb3rko.mensaguthaben2.ValueHolder.data
import java.io.IOException

class Readers : ICardReader {
    private val readers = arrayOf(
        MagnaCartaReader(),
        IntercardReader()
    )

    @Throws(DesfireException::class)
    override fun readCard(card: DesfireProtocol): ValueData? {
        Log.i(TAG, "Trying all readers")
        for (reader in readers) {
            Log.i(TAG, "Trying " + reader.javaClass.simpleName)
            val valueData = reader.readCard(card)
            if (valueData != null) return valueData
        }
        return null
    }

    @Throws(DesfireException::class)
    fun readTag(tag: Tag?): ValueData? {
        Log.i(TAG, "Loading tag")
        val tech = IsoDep.get(tag)
        try {
            tech.connect()
        } catch (e: IOException) {
            //Tag was removed. We fail silently.
            e.printStackTrace()
            return null
        }
        return try {
            val desfireTag = DesfireProtocol(tech)


            //Android has a Bug on Devices using a Broadcom NFC chip. See
            // http://code.google.com/p/android/issues/detail?id=58773
            //A Workaround is to connected to the tag, issue a dummy operation and then reconnect...
            try {
                desfireTag.selectApp(0)
            } catch (e: ArrayIndexOutOfBoundsException) {
                //Exception occurs because the actual response is shorter than the error response
                Log.i(TAG, "Broadcom workaround was needed")
            }
            tech.close()
            tech.connect()
            val valueData = instance!!.readCard(desfireTag)
            data = valueData
            valueData
        } catch (e: IOException) {
            //This can only happen on tag close. we ignore this.
            e.printStackTrace()
            null
        } finally {
            if (tech.isConnected) try {
                tech.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private val TAG = Readers::class.java.name
        var instance: Readers? = null
            get() {
                if (field == null) field = Readers()
                return field!!
            }
            private set
    }
}