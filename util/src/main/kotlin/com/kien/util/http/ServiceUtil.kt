package com.kien.util.http

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.InetAddress
import java.net.UnknownHostException

@Component
class ServiceUtil(
    @Value("\${server.port}") val port: String
) {

    val serviceAddress: String
        get() = findMyHostname() + "/" + findMyIpAddress() + ":" + port

    private fun findMyHostname(): String = try {
        InetAddress.getLocalHost().hostName
    } catch (e: UnknownHostException) {
        "unknown host name"
    }

    private fun findMyIpAddress(): String = try {
        InetAddress.getLocalHost().hostAddress
    } catch (e: UnknownHostException) {
        "unknown IP address"
    }
}
