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

    override fun onCreate(savedInstanceState: Bundle?) {
        // Default behaviour
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        // Set LayoutManager and Adapter for RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_results)
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = resultsAdapter

        // When reloading activity
        if (savedInstanceState != null) {
            resultsList = savedInstanceState.getParcelableArrayList<Device>("resultsList") as ArrayList<Device>
        }

        // Get the progress bar
        progressBar = findViewById(R.id.progress_bar)

        // Initialize Wifi manager
        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        // Start the scan
        startScan()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                refresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Set the menu (actually the refresh button)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList("resultsList", resultsList)
        super.onSaveInstanceState(outState)
    }

    private fun refresh() {
        scanner.stopScan()
        resultsList.clear()
        startScan()
    }

    private fun startScan() {
        Toast.makeText(this, R.string.starting_scan, Toast.LENGTH_LONG).show()
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