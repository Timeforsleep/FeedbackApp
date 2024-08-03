package com.example.feedbackapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.tencent.mmkv.MMKV
import com.tencent.msdk.dns.DnsConfig
import com.tencent.msdk.dns.MSDKDnsResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class App : Application() {
    companion object {
        private lateinit var instance: App

        fun getInstance(): App {
            return instance
        }

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
    // 定义一个应用范围的 CoroutineScope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun onCreate() {
        super.onCreate()
        instance = this
        // 在后台线程初始化 HttpDNS
        applicationScope.launch(Dispatchers.IO) {
            MMKV.initialize(instance.applicationContext)
            val dnsConfig = DnsConfig.Builder()
                .dnsId("45672")
                .dnsKey("bGZq9hw9")
                .desHttp() // (Optional) Log granularity, if debug mode is enabled, pass in "Log.VERBOSE".
                .logLevel(Log.VERBOSE)
                .build()

            MSDKDnsResolver.getInstance().init(this@App, dnsConfig)
        }
    }
}