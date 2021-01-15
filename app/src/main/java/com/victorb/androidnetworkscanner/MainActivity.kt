package com.victorb.androidnetworkscanner

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private var resultsAdapter = ResultsAdapter()

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

        // Get the network prefix length
        val networkPrefixLength: Int = getNetworkPrefixLength(this)

        // Check if Wifi is enabled and connected and starts the scan
        if (checkWiFiEnabled(this)) {
            if (checkWifiConnected(this)) {
                startScan(getPhoneIp(this.applicationContext), networkPrefixLength, 2000, resultsAdapter, this)
            } else {
                Toast.makeText(this, R.string.wifi_not_connected, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, R.string.wifi_not_enabled, Toast.LENGTH_LONG).show()
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
                // TODO: Refresh button
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
}