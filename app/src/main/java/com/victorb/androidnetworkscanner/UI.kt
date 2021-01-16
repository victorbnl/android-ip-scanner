package com.victorb.androidnetworkscanner

import android.content.Context
import android.widget.Toast

fun message(context: Context, stringId: Int) =
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show()

fun refreshingMessage(context: Context) =
        message(context, R.string.refreshing)

fun wifiNotEnabledMessage(context: Context) =
        message(context, R.string.wifi_not_enabled)

fun wifiNotConnectedMessage(context: Context) =
        message(context, R.string.wifi_not_connected)