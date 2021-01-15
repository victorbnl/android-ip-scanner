package com.victorb.androidnetworkscanner

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress

/**
 * Starts a scan
 */
fun startScan(baseIp: Int, checkTimeout: Int, resultsAdapter: ResultsAdapter, activity: Activity) {
    // Create the scope for all the coroutines
    val checkJobsScope = CoroutineScope(Dispatchers.IO)

    // Get the network prefix length
    val networkPrefixLength: Int = getNetworkPrefixLength(baseIp)

    // Reverse the bytes to make operations easier
    val ip: Int = reverseIpBytes(baseIp)

    // Define the lowest ip and highest IP
    val lowestIp: Int = generateLowestIp(ip, networkPrefixLength)
    val highestIp: Int = generateHighestIp(ip, networkPrefixLength)

    // Loop through all possible IPs
    for (ip in lowestIp..highestIp) {

        // Start the scan job
        checkJobsScope.launch {
            // Convert it to an InetAddress object
            val ipAsInetAddress: InetAddress = reversedIpToInetAddress(ip)

            // Check if it's reachable
            if (ipAsInetAddress.isReachable(checkTimeout)) {

                // Define the hostname and the ip to add
                val hostname: String = ipAsInetAddress.hostName
                val ipAsString: String = ipToString(ip)

                // Add its hostname and IP to the recycler view
                activity.runOnUiThread {
                    resultsAdapter.addItem(ipAsString, hostname)
                }
            }
        }
    }
}

/**
 * Generates the lowest po'ssible IP
 * For example if your IP is 192.168.1.1 and your prefix length 16
 * It will generate 192.168.0.0
 */
fun generateLowestIp(ip: Int, networkPrefixLength: Int) =
        ip and (((1 shl networkPrefixLength) - 1) shl (32 - networkPrefixLength))

/**
 * Generates the highest possible IP
 * For example if you have as IP 192.168.1.1 and as prefix length 16
 * It will generate 192.168.255.255
 */
fun generateHighestIp(ip: Int, networkPrefixLength: Int) =
        generateLowestIp(ip, networkPrefixLength) + ((1 shl (32 - networkPrefixLength)) - 1)