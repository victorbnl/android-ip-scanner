package com.victorb.androidnetworkscanner

import android.app.Activity
import android.view.View
import android.widget.ProgressBar
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface

class Scanner(private val phoneIp: Int,
              private val resultsList: MutableList<Device>,
              private val resultsAdapter: ResultsAdapter,
              private val progressBar: ProgressBar,
              private val activity: Activity) {

    // The timeout of the network request
    private val timeout: Int = 2000

    // The main jobs which starts the other jobs
    private lateinit var scanningJob: Job

    // The scope for the checking jobs and a list containing them (used to stop all of them)
    private val checkingJobsScope = CoroutineScope(Dispatchers.IO)
    private val checkingJobs: MutableList<Job> = arrayListOf()

    // The network prefix length
    private var networkPrefixLength: Int = 24

    init {
        val inetAddress: InetAddress = InetAddress.getByAddress(ipToBytes(phoneIp));

        // Get the network interfaces
        val networkInterface: NetworkInterface = NetworkInterface.getByInetAddress(inetAddress);
        val interfaceAddresses: MutableList<InterfaceAddress> = networkInterface.interfaceAddresses

        // Set the network mask to the one of the first IPv4 interface
        for (address in interfaceAddresses) {
            if (address.address is Inet4Address) {
                networkPrefixLength = address.networkPrefixLength.toInt()
            }
        }
    }

    /**
    * Starts the scan
     */
    fun startScan() {
        // Set progress bar on
        setProgressBarVisibility(View.VISIBLE)

        // Start scan
        scanningJob = GlobalScope.launch {
            // Start checking all ips
            checkIps(phoneIp, networkPrefixLength)

            // Wait for all the jobs to finish
            for (job in checkingJobs) {
                job.join()
            }

            // Hide progress bar when finished
            scanningJob.invokeOnCompletion {
                setProgressBarVisibility(View.GONE)
            }
        }
    }

    /**
     * Stops the scan
     */
    fun stopScan() {
        // Start the scan job (which start the recursive scan function)
        scanningJob.cancel()

        // Stop all the checking jobs
        for (job in checkingJobs) job.cancel()

        // Remove progress bar
        setProgressBarVisibility(View.GONE)
    }

    /**
     * The recursive function that scans the network
     */
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

    /**
     * Starts a coroutine which checks the IP in the checkingJobsScope sope
     * because GlobalScope doesn't work as expected
     * https://elizarov.medium.com/the-reason-to-avoid-globalscope-835337445abc
     */
    private fun checkIp(ip: Int) = checkingJobsScope.launch {
        // Define the IP address object
        val inetAddress: InetAddress = InetAddress.getByAddress(ipToBytes(ip))

        // Check if it's reachable
        if (inetAddress.isReachable(timeout)) {
            // Add the device in the results list with its ip and hostname
            resultsList.add(Device(ipToString(ip),
                    if (ip == this@Scanner.phoneIp)
                        "Your phone"
                    else
                        inetAddress.hostName.split(".")[0]))

            // Notify the RecyclerView the list changed
            activity.runOnUiThread {
                resultsAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Shows or hide the progress bar
     */
    private fun setProgressBarVisibility(int: Int) {
        activity.runOnUiThread {
            progressBar.visibility = int
        }
    }

    /**
     * Reverses the ip bytes to make calculations easier
     * UNUSED: Reserved for a future change in the algorithm
     */
    private fun reverseIpBytes(ip: Int): Int =
            ((ip and 0xff) shl 24) +
            ((ip and 0xff) shl 16) +
            ((ip and 0xff) shl 8) +
            (ip and 0xff)

    /**
     * Converts the IP to a human-readable string
     */
    private fun ipToString(ip: Int) = String.format(
        "%d.%d.%d.%d",
        (ip and 0xff),
        (ip shr 8 and 0xff),
        (ip shr 16 and 0xff),
        (ip shr 24 and 0xff)
    )

    /**
     * Converts the IP to a four-byte array
     */
    private fun ipToBytes(ip: Int) = arrayOf(
        (ip and 0xff).toByte(),
        (ip shr 8 and 0xff).toByte(),
        (ip shr 16 and 0xff).toByte(),
        (ip shr 24 and 0xff).toByte()
    ).toByteArray()
}