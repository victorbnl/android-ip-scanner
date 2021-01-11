package com.victorb.androidnetworkscanner

import android.app.Activity
import android.view.View
import android.widget.ProgressBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface

class Scanner(private val ip: Int,
              private val resultsList: MutableList<Device>,
              private val resultsAdapter: ResultsAdapter,
              private val progressBar: ProgressBar,
              private val activity: Activity) {

    private val timeout: Int = 2000
    private lateinit var scanningJob: Job
    private val checkingJobs: MutableList<Job> = arrayListOf()
    private var networkPrefixLength: Int = 24

    init {
        // Get the network prefix length
        networkPrefixLength = 0;
        val inetAddress: InetAddress = InetAddress.getByAddress(ipToBytes(ip));
        val networkInterface: NetworkInterface = NetworkInterface.getByInetAddress(inetAddress);
        val interfaceAddresses: MutableList<InterfaceAddress> = networkInterface.interfaceAddresses
        for (address in interfaceAddresses) if (address.address is Inet4Address) networkPrefixLength =
            address.networkPrefixLength.toInt()
    }

    fun startScan() {
        // Set progress bar on
        setProgressBarVisibility(View.VISIBLE)

        // Start scan
        scanningJob = GlobalScope.launch {
            checkIps(ip, networkPrefixLength)
        }

        // Hide progress bar when finished
        scanningJob.invokeOnCompletion {
            setProgressBarVisibility(View.GONE)
        }
    }

    fun stopScan() {
        // Start the scan job (which start the recursive scan function)
        scanningJob.cancel()

        // Stop all the checking jobs
        for (job in checkingJobs) job.cancel()

        // Remove progress bar
        setProgressBarVisibility(View.GONE)
    }

    // Recursive function to scan the network
    private fun checkIps(ip: Int, index: Int) {
        // If editing the last byte of the ip
        if (index == 24) {
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
        checkingJobs.add(GlobalScope.launch {
            val inetAddress: InetAddress = InetAddress.getByAddress(ipToBytes(ip))
            if (inetAddress.isReachable(timeout)) {
                resultsList.add(Device(ipToString(ip), if (ip == this@Scanner.ip) "Your phone" else inetAddress.hostName.split(".")[0]))
                activity.runOnUiThread {
                    resultsAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setProgressBarVisibility(int: Int) {
        activity.runOnUiThread {
            progressBar.visibility = int
        }
    }

    private fun reverseIpBytes(ip: Int): Int = ((ip and 0xff) shl 24) + ((ip and 0xff) shl 16) + ((ip and 0xff) shl 8) + (ip and 0xff)

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