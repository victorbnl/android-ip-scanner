package com.victorb.androidnetworkscanner

import android.app.Activity
import android.net.DhcpInfo
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface

class Scanner(private val ip: Int,
              private val resultsList: MutableList<Device>,
              private val resultsAdapter: ResultsAdapter,
              private val activity: Activity) {

    private val TIMEOUT: Int = 2000;

    fun startScan() {
        // Get subnet mask
        var networkPrefixLength: Int = 0;
        val inetAddress: InetAddress = InetAddress.getByAddress(ipToBytes(ip));
        val networkInterface: NetworkInterface = NetworkInterface.getByInetAddress(inetAddress);
        val interfaceAddresses: MutableList<InterfaceAddress> = networkInterface.interfaceAddresses
        for (address in interfaceAddresses) if (address.address is Inet4Address) networkPrefixLength =
            address.networkPrefixLength.toInt()

        // Clear results
        resultsList.clear()

        // Start scan
        GlobalScope.launch {
            checkIps(ip, networkPrefixLength)
        }

    }

    // Recursive function to scan the network
    private fun checkIps(ip: Int, index: Int) {
        if (index == 24) { // If editing the last byte of the ip
            for (i: Int in 0..255) {
                checkIp(
                    (ip and
                            ((1 shl index) - 1))
                            + (i shl index)
                )
            }
        } else {
            for (i: Int in 0..255) {
                // Scan next byte
                checkIps(
                    (ip and
                            ((1 shl index) - 1)) // Keep only the unmodified part of the ip
                            + (i shl index),
                    index + 8
                )
            }
        }
    }

    private fun checkIp(ip: Int) {
        GlobalScope.launch {
            val inetAddress: InetAddress = InetAddress.getByAddress(ipToBytes(ip))
            if (inetAddress.isReachable(TIMEOUT)) {
                resultsList.add(Device(ipToString(ip), if (ip == this@Scanner.ip) "Your phone" else inetAddress.hostName.split(".")[0]))
                activity.runOnUiThread {
                    resultsAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun ipToString(ip: Int) = String.format(
        "%d.%d.%d.%d",
        (ip and 0xff),
        (ip shr 8 and 0xff),
        (ip shr 16 and 0xff),
        (ip shr 24 and 0xff)
    )

    private fun ipToBytes(ip: Int) = arrayOf(
        (ip and 0xff).toByte(),
        (ip shr 8 and 0xff).toByte(),
        (ip shr 16 and 0xff).toByte(),
        (ip shr 24 and 0xff).toByte()
    ).toByteArray()
}