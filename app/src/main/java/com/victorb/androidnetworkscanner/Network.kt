package com.victorb.androidnetworkscanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface

fun isWifiEnabled(context: Context): Boolean =
    (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled

fun isPhoneConnected(context: Context): Boolean =
    (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork != null

fun getPhoneIp(context: Context): Int =
        (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).dhcpInfo.ipAddress

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

fun getIpHostname(ip: Int): String = InetAddress.getByAddress(intIpToByteArray(ip)).hostName

fun isIpReachable(ip: Int): Boolean = InetAddress.getByAddress(intIpToByteArray(ip)).isReachable(2000)