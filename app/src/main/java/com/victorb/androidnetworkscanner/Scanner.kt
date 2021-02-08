package com.victorb.androidnetworkscanner

import android.animation.ObjectAnimator
import android.content.Context
import kotlinx.coroutines.*

class Scanner(private val context: Context, private val resultsAdapter: ResultsAdapter, private val animator: ObjectAnimator?) {
    private val scanJobScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val checkingJobsScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var currentScanJob: Job? = null
    private var checkingJobs: MutableList<Job> = arrayListOf()

    fun startScan() {
        if (currentScanJob == null || currentScanJob?.isActive == false) {
            runOnMainThread {
                animator?.start()
            }
            currentScanJob = scanJobScope.launch {
                for (ip in generateIpRange(
                    intIpToReversedIntIp(getPhoneIp(context)),
                    getNetworkPrefixLength(context)
                )) {
                    checkingJobs.add(checkingJobsScope.launch {
                        val reversedIp: Int = intIpToReversedIntIp(ip)
                        println(intIpToString(reversedIp))
                        if (isIpReachable(reversedIp)) {
                            val hostname: String = getIpHostname(reversedIp)
                            val ipString: String = intIpToString(reversedIp)
                            resultsAdapter.addItem(ipString, hostname)
                        }
                    })
                }
                checkingJobs.joinAll()
                println("Scan finished")
                runOnMainThread {
                    animator?.cancel()
                }
            }
        }
    }
}