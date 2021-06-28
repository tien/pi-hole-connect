package com.tien.piholeconnect.util

import okhttp3.Dns
import java.net.Inet4Address
import java.net.InetAddress

/**
 * Needed as pi.hole IPv6 can change, which will lead to time out when "pi.hole" domain
 * is resolved to an incorrect IPv6 address. The user will need to update their pi.hole
 * IPv6 address anyway, in order for IPv6 DNS resolution to work correctly for other sites
 * but that should probably not prevent them from using this application
 * hence we want to default to IPv4
 */
class Ipv4FirstDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> =
        Dns.SYSTEM.lookup(hostname).sortedByDescending { it is Inet4Address }
}
