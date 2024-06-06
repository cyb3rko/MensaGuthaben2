/*
 * Utils.java
 *
 * Copyright (C) 2011 Eric Butler
 *
 * Authors:
 * Eric Butler <eric@codebutler.com>
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

package com.codebutler.farebot;

import android.util.Log;

import com.codebutler.farebot.card.desfire.DesfireException;
import com.codebutler.farebot.card.desfire.DesfireFileSettings;
import com.codebutler.farebot.card.desfire.DesfireProtocol;

public class Utils {
	private static final String TAG = Utils.class.getName();
    
    public static int byteArrayToInt(byte[] b) {
        return byteArrayToInt(b, 0);
    }
    
    public static int byteArrayToInt(byte[] b, int offset) {
        return byteArrayToInt(b, offset, b.length);
    }
    
    public static int byteArrayToInt(byte[] b, int offset, int length) {
        return (int) byteArrayToLong(b, offset, length);
    }

    public static long byteArrayToLong(byte[] b, int offset, int length) {
        if (b.length < length)
            throw new IllegalArgumentException("length must be less than or equal to b.length");

        long value = 0;
        for (int i = 0; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (long) (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    public static DesfireFileSettings selectAppFile(DesfireProtocol tag, int appID, int fileID) {
		try {
			tag.selectApp(appID);
		} catch (DesfireException e) {
			Log.w(TAG,"App not found");
			return null;
		}
		try {
			return tag.getFileSettings(fileID);
		} catch (DesfireException e) {
			Log.w(TAG,"File not found");
			return null;
		}
	}
}
