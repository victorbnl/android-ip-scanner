package com.victorb.androidnetworkscanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface

/**
 * Get the Wifi Manager service
 *
 * @param context The application context
 */
fun getWifiManager(context: Context): WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager

/**
 * Check if WiFi is enabled
 *
 * @param context The context used to get the WifiManager. Must be an application context
 * @return Whether Wifi is enabled or not, as a Boolean
 */
fun isWifiEnabled(context: Context): Boolean =
        getWifiManager(context).isWifiEnabled

/**
 * Check if Wifi is connected
 * TODO: Find a way to check if WiFi is connected
 *
 * @param context The context used to get the WifiManager. Must be an application context
 */
fun isWifiConnected(context: Context): Boolean =
    true

/**
 * Get the phone IP
 *
 * @param context The context used to get the WifiManager. Must be an application context
 * @return The phone IP as an Int
 */
fun getPhoneIp(context: Context): Int =
        (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).dhcpInfo.ipAddress

/**
 * Get the network prefix length from the phone IP
 *
 * @param context The context used to get the WifiManager. Must be an application context
 * @return The network prefix length as Int
 */
fun getNetworkPrefixLength(context: Context): Int {
    // IP object
    val inetAddress: InetAddress = InetAddress.getByAddress(intIpToByteArray(getPhoneIp(context)));

    // Get the network interfaces
    val networkInterface: NetworkInterface = NetworkInterface.getByInetAddress(inetAddress);
    val interfaceAddresses: MutableList<InterfaceAddress> = networkInterface.interfaceAddresses

    // Set the network mask to the one of the first IPv4 interface
    for (address in interfaceAddresses) {
        if (address.address is Inet4Address) {
            return address.networkPrefixLength.toInt()
        }
    }

    return -1
}