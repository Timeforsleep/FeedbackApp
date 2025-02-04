package com.example.feedbackapp.net

import com.tencent.msdk.dns.MSDKDnsResolver
import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException

class HttpDns : Dns {

    @Throws(UnknownHostException::class)
    override fun lookup(hostname: String): List<InetAddress> {
        val ips = MSDKDnsResolver.getInstance().getAddrByName(hostname)
        val ipArr = ips.split(";")
        if (ipArr.isEmpty()) {
            return emptyList()
        }

        val inetAddressList = mutableListOf<InetAddress>()
        for (ip in ipArr) {
            if (ip == "0") {
                continue
            }
            try {
                val inetAddress = InetAddress.getByName(ip)
                inetAddressList.add(inetAddress)
            } catch (ignored: UnknownHostException) {
                // 忽略解析失败的 IP 地址
            }
        }
        return inetAddressList;
    }
}
