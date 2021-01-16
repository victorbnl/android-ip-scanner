package com.victorb.androidnetworkscanner

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.InetAddress

/**
 * Starts a scan
 *
 * @param scanScope The scope in which run all the coroutines
 * @param baseIp The base IP to start from, can be any IP on the network
 * @param networkPrefixLength The network prefix length to know which IPs to test
 * @param checkTimeout The timeout of the ping request
 * @param resultsAdapter The adapter in which add the results. Must have a addItem(String, String)
 *                       function
 * @param activity Used to run on the UI thread. I'll try to change the algorithm not to need this
 *                 then
 */
fun startScan(baseIp: Int,
              networkPrefixLength: Int,
              checkTimeout: Int,
              resultsAdapter: ResultsAdapter,
              activity: Activity): CoroutineScope {
    // Create the scope for all the coroutines
    val scanScope = CoroutineScope(Dispatchers.IO)

    // Reverse the bytes to make operations easier
    val ip: Int = intIpToReversedIntIp(baseIp)

    // Define the lowest ip and highest IP
    val lowestIp: Int = generateLowestIp(ip, networkPrefixLength)
    val highestIp: Int = generateHighestIp(ip, networkPrefixLength)

    // Loop through all possible IPs
    for (ip in lowestIp..highestIp) {

        // Start the scan job
        scanScope.launch {
            // Convert it to an InetAddress object
            val ipAsInetAddress: InetAddress = reversedIntIpToInetAddress(ip)

            // Check if it's reachable
            if (ipAsInetAddress.isReachable(checkTimeout)) {

                // Define the hostname and the ip to add
                val hostname: String = ipAsInetAddress.hostName.replace(".home", "")
                val ipAsString: String = intReversedIpToString(ip)

                // Add its hostname and IP to the recycler view
                activity.runOnUiThread {
                    resultsAdapter.addItem(ipAsString, hostname)
                }
            }
        }
    }

    return scanScope
}

/**
 * Generates the lowest po'ssible IP
 * For example if your IP is 192.168.1.1 and your prefix length 16
 * It will generate 192.168.0.0
 *
 * @param ip The IP to start from
 * @param networkPrefixLength The network prefix length, which defines which part is constant
 * and which is variables
 * @return The IP as Int with non-constant bytes replaced by 0
 */
fun generateLowestIp(ip: Int, networkPrefixLength: Int) =
        ip and (((1 shl networkPrefixLength) - 1) shl (32 - networkPrefixLength))

/**
 * Generates the highest possible IP
 * For example if you have as IP 192.168.1.1 and as prefix length 16
 * It will generate 192.168.255.255
 *
 * @param ip The IP to start from
 * @param networkPrefixLength The network prefix length, which defines which part is constant
 * and which is variables
 * @return The IP as Int with non-constant bytes replaced by 255
 */
fun generateHighestIp(ip: Int, networkPrefixLength: Int) =
        generateLowestIp(ip, networkPrefixLength) + ((1 shl (32 - networkPrefixLength)) - 1)