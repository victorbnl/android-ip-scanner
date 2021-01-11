package com.victorb.androidnetworkscanner

import android.os.Parcel
import android.os.Parcelable

data class Device(
        val ip: String?,
        val hostname: String?
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(ip)
                parcel.writeString(hostname)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Device> {
                override fun createFromParcel(parcel: Parcel): Device {
                        return Device(parcel)
                }

                override fun newArray(size: Int): Array<Device?> {
                        return arrayOfNulls(size)
                }
        }
}
