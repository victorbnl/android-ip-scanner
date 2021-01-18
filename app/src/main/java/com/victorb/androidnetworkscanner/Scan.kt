package com.victorb.androidnetworkscanner

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnCancel
import kotlinx.coroutines.*
import java.net.InetAddress

fun startScan(context: Context, activity: Activity, viewToRotate: View?, adapter: ResultsAdapter): Job =
CoroutineScope(Dispatchers.IO).launch {
    if ((context.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager).isWifiEnabled) {
        if (true) { // TODO: Check if device is connected
            val networkPrefixLength: Int = getNetworkPrefixLength(context)
            val reversedIp: Int = intIpToReversedIntIp(getPhoneIp(context))
            val ipRange: IntRange = generateIpRange(reversedIp, networkPrefixLength)
            val jobs: ArrayList<Job> = arrayListOf()
            val animator: ObjectAnimator = ObjectAnimator.ofFloat(viewToRotate, "rotation", 360f).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                interpolator = LinearInterpolator()
                doOnCancel {
                    viewToRotate?.rotation = 0f
                }

            }
            activity.runOnUiThread {
                animator.start()
            }
            for (ipToTest in ipRange) {
                val reversedIpToTest: Int = intIpToReversedIntIp(ipToTest)
                jobs.add(CoroutineScope(Dispatchers.IO).launch {
                    val ipToTestAsInetAddress = InetAddress.getByAddress(intIpToByteArray(reversedIpToTest))
                    if (ipToTestAsInetAddress.isReachable(2000)) {
                        val ipAsString: String = intIpToString(reversedIpToTest)
                        val gotHostname: String = ipToTestAsInetAddress.hostName
                        val hostname: String = if (gotHostname != ipAsString) gotHostname.replace(".home", "") else context.getString(R.string.unknown_device)
                        activity.runOnUiThread {
                            adapter.addItem(ipAsString, hostname)
                        }
                    }
                })
            }
            jobs.joinAll()
            activity.runOnUiThread {
                animator.cancel()
            }
        } else {
            activity.runOnUiThread {
                Toast.makeText(context, R.string.wifi_not_connected, Toast.LENGTH_LONG).show()
            }
        }
    } else {
        activity.runOnUiThread {
            Toast.makeText(context, R.string.wifi_not_enabled, Toast.LENGTH_LONG).show()
        }
    }
}