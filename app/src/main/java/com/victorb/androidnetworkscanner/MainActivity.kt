package com.victorb.androidnetworkscanner

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private var resultsAdapter = ResultsAdapter()
    private lateinit var scanningJob: Job

    /**
     * The main function
     * Sets up the recycler view, initializes WiFi and starts the scan
     */
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

        //toolbar.findViewById<Button>(R.id.action_refresh).rotation = 90f

        /*ObjectAnimator.ofFloat(toolbar.findViewById<Button>(R.id.action_refresh), "rotationY", 100f).apply {
            start()
        }*/

        scanningJob = startScan(this, this, findViewById(R.id.progress_bar), resultsAdapter)
    }

    private fun startScan(context: Context, activity: Activity, progressBar: ProgressBar, adapter: ResultsAdapter): Job =
            CoroutineScope(Dispatchers.IO).launch {
            if (isWifiEnabled(context)) {
                if (isWifiConnected(context)) {
                    val networkPrefixLength: Int = getNetworkPrefixLength(context)
                    val reversedIp: Int = intIpToReversedIntIp(getPhoneIp(context))
                    val ipRange: IntRange = generateIpRange(reversedIp, networkPrefixLength)
                    val jobs: ArrayList<Job> = arrayListOf()
                    activity.runOnUiThread {
                        progressBar.progress = 0
                        progressBar.max = ipRange.count()
                        progressBar.visibility = View.VISIBLE
                    }
                    for (ipToTest in ipRange) {
                        val reversedIpToTest: Int = intIpToReversedIntIp(ipToTest)
                        jobs.add(CoroutineScope(Dispatchers.IO).launch {
                            val ipToTestAsInetAddress = InetAddress.getByAddress(intIpToByteArray(reversedIpToTest))
                            println("Testing " + intReversedIpToString(ipToTest))
                            if (ipToTestAsInetAddress.isReachable(2000)) {
                                val ipAsString: String = intIpToString(reversedIpToTest)
                                val gotHostname: String = ipToTestAsInetAddress.hostName
                                val hostname: String = if (gotHostname != ipAsString) gotHostname.replace(".home", "") else getString(R.string.unknown_device)
                                activity.runOnUiThread {
                                    adapter.addItem(ipAsString, hostname)
                                }
                            }
                            activity.runOnUiThread {
                                progressBar.progress = progressBar.progress + 1
                            }
                        })
                    }
                    jobs.joinAll()
                    activity.runOnUiThread {
                        progressBar.visibility = View.GONE
                    }
                } else {
                    wifiNotConnectedMessage(context)
                }
            } else {
                wifiNotEnabledMessage(context)
            }
    }

    /**
     * Define actions for toolbar buttons
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Control menu buttons (toolbar buttons)
        when (item.itemId) {
            // When refresh menu button clicked
            R.id.action_refresh -> {
                val context: Context = this
                val activity: Activity = this
                CoroutineScope(Dispatchers.Default).launch {
                    scanningJob.cancelAndJoin()
                    println("Refresh")
                    activity.runOnUiThread {
                        resultsAdapter.clearList()
                    }
                    scanningJob = startScan(context, activity, findViewById(R.id.progress_bar), resultsAdapter)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Sets the menu to use for the toolbar buttons
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Set the menu (actually the refresh button)
        menuInflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.action_refresh)?.actionView?.rotation = 90f
        return true
    }
}