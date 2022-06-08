@file:Suppress("unused")

package com.binzee.foxlib.lib_kotlin.utils.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.binzee.foxlib.lib_kotlin.FoxCore
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * 网络状态枚举
 */
enum class NetworkType(val level: Int) {
    NONE(0),
    DATA(1),

    //    NR5G(2),
    WIFI(3)
}

/**
 * NetworkStatus
 *
 * 网络状态工具
 * @since  2022/6/8 10:32
 * @author Administrator
 */
object NetworkStatus {

    // 网络服务
    private val connectivityManager: ConnectivityManager?
        get() = FoxCore.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    /**
     * 网络链接状态
     *
     * @see NetworkType
     */
    val networkType: NetworkType
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        get() {
            connectivityManager ?: return NetworkType.NONE  // 无网络
            val network = connectivityManager?.activeNetwork
            network ?: return NetworkType.NONE  // 无网络
            val capabilities = connectivityManager?.getNetworkCapabilities(network)
            capabilities ?: return NetworkType.NONE // 无网络
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                    NetworkType.DATA
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                    NetworkType.WIFI
                else -> NetworkType.NONE
            }
        }

    /**
     * 局域网IP地址
     */
    val lanIPAddress: String
        get() {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddr = enumIpAddr.nextElement()
                    if (!inetAddr.isLoopbackAddress && inetAddr is Inet4Address) {
                        return inetAddr.getHostAddress()?.toString() ?: ""
                    }
                }
            }
            return ""
        }

    /**
     * 外网IP
     */
    val publicIPAddress: String
        get() = when (networkType) {
            NetworkType.NONE -> ""
            NetworkType.DATA -> getDataIPAddress()
            NetworkType.WIFI -> getWifiIPAddress()
        }

    /**
     * 注册网络监听器
     *
     * @param callback  网络监听器
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun registerNetworkListener(callback: ConnectivityManager.NetworkCallback) {
        connectivityManager?.registerDefaultNetworkCallback(callback)
    }

    /**
     * 注销网络监听器
     */
    fun unregisterNetworkListener(callback: ConnectivityManager.NetworkCallback) {
        connectivityManager?.unregisterNetworkCallback(callback)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取移动网络的IP
     */
    private fun getDataIPAddress(): String {
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val intf = en.nextElement()
            val enumIpAddr = intf.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress) {
                    return inetAddress?.hostAddress ?: ""
                }
            }
        }
        return ""
    }

    /**
     * 获取WIFI IP地址
     */
    private fun getWifiIPAddress(): String {
        val info = (FoxCore.appContext.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
        val ip = info.ipAddress
        return intToIp(ip)
    }

    /**
     * ip转字符
     *
     */
    private fun intToIp(intIp: Int): String {
        return (intIp shr 24).toString() + "." +
                (intIp and 0x00FFFFFF shr 16) + "." +
                (intIp and 0x0000FFFF shr 8) + "." +
                (intIp and 0x000000FF)
    }
}