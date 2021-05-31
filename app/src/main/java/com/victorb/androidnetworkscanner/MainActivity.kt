package com.victorb.androidnetworkscanner

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnCancel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    // UI
    private var animator: ObjectAnimator? = null
    private var resultsAdapter = ResultsAdapter()

    // Coroutines
    private val scanJobScope = CoroutineScope(Dispatchers.Default)
    private var currentScanJob: Job? = null
    private val checkJobsScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Default behaviour
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        // Set the LayoutManager and the Adapter for RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_results)
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = resultsAdapter

        // Start a scan
        // The delay is necessary to find the toolbar refresh button
        // See : https://stackoverflow.com/questions/28840815/menu-item-animation-rotate-indefinitely-its-custom-icon
        runOnMainThreadDelayed(100) {
            // Create the animator
            val view = findViewById<View>(R.id.action_refresh)
            animator = ObjectAnimator.ofFloat(view, "rotation", 360f).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                interpolator = LinearInterpolator()
                doOnCancel {
                    view.rotation = 0f
                }
            }

            // Start the scan
            currentScanJob = startScan(this)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Control menu buttons (toolbar buttons)
        when (item.itemId) {
            // When refresh menu button clicked
            R.id.action_refresh -> {
                resultsAdapter.clear()
                currentScanJob = startScan(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Set the menu (actually the refresh button)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun startScan(context: Context): Job =
        scanJobScope.launch {
            // Check if wifi is on
            if (isWifiEnabled(context) && isWifiConnected(context)) {
                // Start the refresh button animation
                runOnMainThread { animator?.start() }
                // List of check jobs
                val checkingJobs: ArrayList<Job> = arrayListOf()
                // Iterate through all the possible IPs
                for (ip in generateIpRange(
                    intIpToReversedIntIp(getPhoneIp(context)),
                    getNetworkPrefixLength(context)
                )) {
                    // Add the jobs, which checks if the connection is up and adds it to the adapter
                    checkingJobs.add(checkJobsScope.launch {
                        val reversedIp: Int = intIpToReversedIntIp(ip)
                        if (isIpReachable(reversedIp)) {
                            val hostname: String = getIpHostname(reversedIp)
                            val ipString: String = intIpToString(reversedIp)
                            resultsAdapter.addItem(ipString, hostname)
                        }
                    })
                }
                // Wait for the checking jobs to finish
                checkingJobs.joinAll()
                // Stop the animation
                runOnMainThread { animator?.cancel() }
            // Wifi is off
            } else {
                runOnMainThread { Toast.makeText(context, "Please enable Wifi and connect to an access point", Toast.LENGTH_LONG).show() }
            }
        }
}