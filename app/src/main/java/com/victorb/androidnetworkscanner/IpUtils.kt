package com.victorb.androidnetworkscanner

import java.net.InetAddress

/**
 * Reverses the ip bytes to make calculations easier
 * For example 1.2.3.4 becomes 4.3.2.1
 *
 * @param ip The IP to reverse bytes
 * @return The reversed IP as Int
 */
fun intIpToReversedIntIp(ip: Int): Int =
        ((ip and 0xff) shl 24) +
                (((ip shr 8) and 0xff) shl 16) +
                (((ip shr 16) and 0xff) shl 8) +
                ((ip shr 24) and 0xff)

/**
 * Converts the IP to a human-readable string
 * For example 16909060 becomes 1.2.3.4
 *
 * @param ip The IP to convert
 * @return The IP as String with 1.2.3.4 format
 */
fun intIpToString(ip: Int) = String.format(
        "%d.%d.%d.%d",
        (ip and 0xff),
        (ip shr 8 and 0xff),
        (ip shr 16 and 0xff),
        (ip shr 24 and 0xff)
)

fun intReversedIpToString(ip: Int) = String.format(
        "%d.%d.%d.%d",
        (ip shr 24 and 0xff),
        (ip shr 16 and 0xff),
        (ip shr 8 and 0xff),
        (ip and 0xff)

)

/**
 * Converts the IP to a four-byte array
 * For example 16909060 becomes [1, 2, 3, 4]
 *
 * @param ip The IP to convert
 * @return The ByteArray containing each byte of the IP
 */
fun intIpToByteArray(ip: Int): ByteArray = arrayOf(
        (ip and 0xff).toByte(),
        (ip shr 8 and 0xff).toByte(),
        (ip shr 16 and 0xff).toByte(),
        (ip shr 24 and 0xff).toByte()
).toByteArray()

/**
 * Reverse the IP and converts it to an array of bytes at the same time
 * For example 16909060 becomes [4, 3, 2, 1]
 *
 * @param ip The IP to convert
 * @return The reversed ByteArray containing each byte of the IP
 */
fun intIpToReversedByteArray(ip: Int): ByteArray = arrayOf(
        (ip shr 24 and 0xff).toByte(),
        (ip shr 16 and 0xff).toByte(),
        (ip shr 8 and 0xff).toByte(),
        (ip and 0xff).toByte()
).toByteArray()

/**
 * Reverses the IP and converts it to a InetAddress object
 * Used in the main for loop where IPs are reversed to make the requests
 *
 * @param ip The reversed IP to convert
 * @return The InetAddress object from the IP
 */
fun reversedIntIpToInetAddress(ip: Int): InetAddress = InetAddress.getByAddress(intIpToReversedByteArray(ip))

fun generateIpRange(ip: Int, networkPrefixLength: Int): IntRange {
    val lowestIp: Int = ip and (((1 shl networkPrefixLength) - 1) shl (32 - networkPrefixLength))
    val highestIp: Int = lowestIp + ((1 shl (32 - networkPrefixLength)) - 1)
    return lowestIp..highestIp
}