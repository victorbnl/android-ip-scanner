package com.victorb.androidnetworkscanner

import java.net.InetAddress

/**
 * Reverses the ip bytes to make calculations easier
 * UNUSED: Reserved for a future change in the algorithm
 */
fun reverseIpBytes(ip: Int): Int =
        ((ip and 0xff) shl 24) +
                (((ip shr 8) and 0xff) shl 16) +
                (((ip shr 16) and 0xff) shl 8) +
                ((ip shr 24) and 0xff)

/**
 * Converts the IP to a human-readable string
 */
fun ipToString(ip: Int) = String.format(
        "%d.%d.%d.%d",
        (ip and 0xff),
        (ip shr 8 and 0xff),
        (ip shr 16 and 0xff),
        (ip shr 24 and 0xff)
)

/**
 * Converts the IP to a four-byte array
 */
fun ipToBytes(ip: Int): ByteArray = arrayOf(
        (ip and 0xff).toByte(),
        (ip shr 8 and 0xff).toByte(),
        (ip shr 16 and 0xff).toByte(),
        (ip shr 24 and 0xff).toByte()
).toByteArray()

/**
 * Reverse the IP and converts it to an array of bytes at the same time
 */
fun ipToReversedBytes(ip: Int): ByteArray = arrayOf(
        (ip shr 24 and 0xff).toByte(),
        (ip shr 16 and 0xff).toByte(),
        (ip shr 8 and 0xff).toByte(),
        (ip and 0xff).toByte()
).toByteArray()

/**
 * Converts an IP to a InetAddress object.
 * Used to makes requests on IP to check its reachability or get its hostname
 */
fun ipToInetAddress(ip: Int): InetAddress = InetAddress.getByAddress(ipToBytes(ip))

/**
 * Converts a reversed IP to a InetAddress object
 * Used in the main for loop where IPs are reversed to be able to check if it's reachable and get
 * its hostname
 */
fun reversedIpToInetAddress(ip: Int): InetAddress = InetAddress.getByAddress(ipToReversedBytes(ip))