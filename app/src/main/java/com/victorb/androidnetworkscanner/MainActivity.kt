package com.victorb.androidnetworkscanner

import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.getSystemService
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var scanner: Scanner;
    private var resultsList: ArrayList<Device> = arrayListOf()
    private var resultsAdapter = ResultsAdapter(resultsList)
    private lateinit var wifiManager: WifiManager
    private lateinit var progressBar: ProgressBar

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

        // Get the progress bar
        progressBar = findViewById(R.id.progress_bar)

        // Initialize Wifi manager
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        // Start the scan
        startScan()
    }

    /**
     * Define actions for toolbar buttons
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Control menu buttons (toolbar buttons)
        when (item.itemId) {
            // When refresh menu button clicked
            R.id.action_refresh -> {
                // Stops the scan, clears the list and starts a new scan
                refresh()
                return true
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
        return true
    }

    /**
     * Stops the scan, clears the list and start a new scan
     */
    private fun refresh() {
        // Stop the scan
        scanner.stopScan()

        // Clear the list
        resultsList.clear()
        resultsAdapter.notifyDataSetChanged()

        // Start the scan
        startScan()
    }

    /**
     * Starts a scan (only if wifi is enabled)
     */
    private fun startScan() {
        // Check if Wifi is enabled
        if (wifiManager.isWifiEnabled) {
            // Start scan
            scanner = Scanner(wifiManager.dhcpInfo.ipAddress, resultsList, resultsAdapter, progressBar, this@MainActivity)
            scanner.startScan()
        } else {
            // Wifi disabled
            Toast.makeText(this, R.string.wifi_not_enabled, Toast.LENGTH_LONG).show()
        }
    }
}