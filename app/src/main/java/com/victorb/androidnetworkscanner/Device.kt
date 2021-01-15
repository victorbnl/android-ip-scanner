package com.victorb.androidnetworkscanner

import android.os.Parcel
import android.os.Parcelable

data class Device(
        val ip: String?,
        val hostname: String?
)