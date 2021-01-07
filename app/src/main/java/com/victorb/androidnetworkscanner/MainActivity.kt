package com.victorb.androidnetworkscanner

import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var scanner: Scanner;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar)

        // Set the list containing the results
        var resultsList: MutableList<Device> = mutableListOf()

        // Set Adapter for RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview_results)
        val resultsAdapter: ResultsAdapter = ResultsAdapter(resultsList)
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = resultsAdapter

        // Get ip
        val wifiManager: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        // Start scan
        scanner = Scanner(wifiManager.dhcpInfo.ipAddress, resultsList, resultsAdapter, this@MainActivity)
        scanner.startScan()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_refresh -> {
                Toast.makeText(this, "Refreshing list...", Toast.LENGTH_SHORT).show()
                scanner.stopScan()
                scanner.clearList()
                scanner.startScan()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }
}