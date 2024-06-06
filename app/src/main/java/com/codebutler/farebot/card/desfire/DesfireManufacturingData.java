/*
 * DesfireManufacturingData.java
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

package com.codebutler.farebot.card.desfire;

import android.os.Parcel;
import android.os.Parcelable;

public class DesfireManufacturingData implements Parcelable {
    public final int hwVendorID;
    public final int hwType;
    public final int hwSubType;
    public final int hwMajorVersion;
    public final int hwMinorVersion;
    public final int hwStorageSize;
    public final int hwProtocol;

    public final int swVendorID;
    public final int swType;
    public final int swSubType;
    public final int swMajorVersion;
    public final int swMinorVersion;
    public final int swStorageSize;
    public final int swProtocol;

    public final int uid;
    public final int batchNo;
    public final int weekProd;
    public final int yearProd;

    private DesfireManufacturingData (Parcel parcel) {
        hwVendorID     = parcel.readInt();
        hwType         = parcel.readInt();
        hwSubType      = parcel.readInt();
        hwMajorVersion = parcel.readInt();
        hwMinorVersion = parcel.readInt();
        hwStorageSize  = parcel.readInt();
        hwProtocol     = parcel.readInt();

        swVendorID     = parcel.readInt();
        swType         = parcel.readInt();
        swSubType      = parcel.readInt();
        swMajorVersion = parcel.readInt();
        swMinorVersion = parcel.readInt();
        swStorageSize  = parcel.readInt();
        swProtocol     = parcel.readInt();

        uid      = parcel.readInt();
        batchNo  = parcel.readInt();
        weekProd = parcel.readInt();
        yearProd = parcel.readInt();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(hwVendorID);
        parcel.writeInt(hwType);
        parcel.writeInt(hwSubType);
        parcel.writeInt(hwMajorVersion);
        parcel.writeInt(hwMinorVersion);
        parcel.writeInt(hwStorageSize);
        parcel.writeInt(hwProtocol);

        parcel.writeInt(swVendorID);
        parcel.writeInt(swType);
        parcel.writeInt(swSubType);
        parcel.writeInt(swMajorVersion);
        parcel.writeInt(swMinorVersion);
        parcel.writeInt(swStorageSize);
        parcel.writeInt(swProtocol);

        parcel.writeInt(uid);
        parcel.writeInt(batchNo);
        parcel.writeInt(weekProd);
        parcel.writeInt(yearProd);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<DesfireManufacturingData> CREATOR = new Parcelable.Creator<DesfireManufacturingData>() {
        public DesfireManufacturingData createFromParcel(Parcel source) {
            return new DesfireManufacturingData(source);
        }

        public DesfireManufacturingData[] newArray(int size) {
            return new DesfireManufacturingData[size];
        }
    };
}