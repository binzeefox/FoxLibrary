package com.binzee.foxlib.lib_kotlin.utils.network.lan

import com.binzee.foxlib.lib_kotlin.utils.async.ThreadPoolUtil
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog
import com.binzee.foxlib.lib_kotlin.utils.network.NetworkStatus
import java.net.InetAddress

/**
 * LanScanner
 *
 * 局域网扫描器
 * @since  2022/6/8 10:29
 * @author Administrator
 */
class LanScanner(
    private val myAddress: String = NetworkStatus.lanIPAddress
) {
    companion object {
        private const val TAG = "LanScanner"
    }

    private val loopThread = ThreadPoolUtil.bySingleThread()
    private val workThread = ThreadPoolUtil.by(8, 16)

    @Volatile
    private var isRecycled: Boolean = false

    /**
     * 回收
     */
    fun recycle() {
        if (isRecycled) {
            FoxLog.i(TAG, "recycle: 已回收")
        } else {
            workThread.dispose()
            loopThread.dispose()
            isRecycled = true
        }
    }

    /**
     * 开始局域网网段扫描
     *
     * @param onResponseListener    扫描结果回调，默认无操作
     */
    fun scan(onResponseListener: (InetAddress) -> Unit = {}) {
        val addrPre = myAddress.substring(0, myAddress.lastIndexOf("."))

        var i = 1
        while (i <= 255 && !isRecycled) {
            val ip = "${addrPre}.$i"
            if (ip != myAddress) checkIpReachable(ip, onResponseListener)
            i++
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 检查IP是否可达
     */
    private fun checkIpReachable(ip: String, listener: (InetAddress) -> Unit) {
        workThread.execute {
            val address = InetAddress.getByName(ip)
            val reachable = address.isReachable(3000)
            if (reachable) listener(address)
            logger(ip, reachable)
        }
    }

    /**
     * 日志
     */
    private fun logger(ip: String, reachable: Boolean) {
        synchronized(TAG) {
            FoxLog.v(TAG, "pingIp: ----------------------------------------")
            FoxLog.v(TAG, "pingIp: ip = $ip")
            FoxLog.v(TAG, "pingIp: result = $reachable")
            FoxLog.v(TAG, "pingIp: ----------------------------------------")
        }
    }
}