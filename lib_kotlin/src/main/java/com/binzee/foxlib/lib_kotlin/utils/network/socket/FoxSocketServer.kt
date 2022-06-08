@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.binzee.foxlib.lib_kotlin.utils.network.socket

import com.binzee.foxlib.lib_kotlin.utils.async.ThreadPoolUtil
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * FoxSocketServer
 *
 * 简单socket服务器实现
 * @since  2022/6/8 11:51
 * @author Administrator
 *
 * @param port  开启的端口号
 */
class FoxSocketServer(private val port: Int, callbacks: Collection<Callback>) {

    companion object {
        private const val TAG = "FoxSocketServer"
        private const val HEART_BEAT_PERIOD = 30000L
    }

    /**
     * 服务端回调
     */
    interface Callback {

        /**
         * 服务器开启回调
         */
        fun onServerStart()

        /**
         * 客户端连接回调
         */
        fun onOpen(client: FoxSocketClient)

        /**
         * 收取客户端消息回调
         */
        fun onMessage(client: FoxSocketClient, message: String)

        /**
         * 客户端关闭回调
         */
        fun onClose(client: FoxSocketClient)

        /**
         * 客户端异常回调
         */
        fun onError(client: FoxSocketClient?, ex: Exception?)

        /**
         * 服务器异常
         */
        fun onServerError(ex: Exception?)

        /**
         * 服务端停止
         */
        fun onServerStop()
    }

    @Volatile
    var isRunning = false
        private set(value) {
            if (!value) {
                if (!server.isClosed) server.close()
                invokeCallback {
                    it.onServerStop()
                }
            }
            field = value
        }

    // 实际server实例
    private lateinit var server: ServerSocket

    // 回调集合
    private val callbackSet = Collections.synchronizedSet(HashSet<Callback>())

    // 工作线程，其中核心线程分别为server线程，accept线程和心跳线程
    private val workExecutor =
        ThreadPoolUtil.by(3, cpuCoreCount, 3, TimeUnit.SECONDS, LinkedBlockingQueue())

    // CPU 核心数
    private val cpuCoreCount: Int get() = Runtime.getRuntime().availableProcessors()

    // 已经获取的socket合集
    private val socketSet = Collections.synchronizedSet(HashSet<FoxSocketClient>())

    init {
        callbackSet.addAll(callbacks)
    }

    /**
     * 添加回调
     */
    fun addCallback(callback: Callback) {
        callbackSet.add(callback)
    }

    /**
     * 注销回调
     */
    fun removeCallback(callback: Callback) {
        callbackSet.remove(callback)
    }

    /**
     * 开启服务
     */
    fun start() {
        isRunning = true
        workExecutor.execute {
            server = ServerSocket(port)

            // 心跳
            workExecutor.execute {
                try {
                    runInLoop {
                        broadcast("heartbet: ${System.currentTimeMillis()}")
                        Thread.sleep(HEART_BEAT_PERIOD)
                    }
                } catch (e: Exception) {
                    FoxLog.e(TAG, "heartbeat error: ", e)
                    isRunning = false
                }
            }

            // accept
            workExecutor.execute {
                invokeCallback {
                    it.onServerStart()
                }

                try {
                    runInLoop({ isRunning }) {
                        // 循环accept
                        val socket = server.accept()
                        val client = FoxSocketClient(socket, listOf(ClientCallback(callbackSet)))
                        socketSet.add(client)
                    }
                } catch (e: Exception) {
                    FoxLog.e(TAG, "accept loop error: ", e)
                    invokeCallback {
                        it.onServerError(e)
                    }
                }
            }
        }
    }

    /**
     * 关闭服务
     */
    fun close() {
        isRunning = false
    }

    /**
     * 广播信息
     */
    fun broadcast(message: String) {
        for (client in socketSet)
            client.send(message)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 自动补货中断异常的循环
     *
     * @param condition 循环条件
     * @param block 循环体
     */
    private fun runInLoop(condition: () -> Boolean = { true }, block: () -> Unit) {
        try {
            while (condition.invoke()) {
                block.invoke()
            }
        } catch (e: InterruptedException) {
            FoxLog.e(TAG, "runInLoop: ", e)
        }
    }

    /**
     * 调用所有的回调
     */
    private fun invokeCallback(block: (callback: Callback) -> Unit) {
        for (c in callbackSet)
            block.invoke(c)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 客户端回调
     *
     * @param callbacks 宿主类的回调容器
     */
    private class ClientCallback(private val callbacks: Collection<Callback>) :
        FoxSocketClient.Callback {

        override fun onOpen(client: FoxSocketClient) {
            invokeCallback {
                it.onOpen(client)
            }
        }

        override fun onMessage(client: FoxSocketClient, message: String) {
            invokeCallback {
                it.onMessage(client, message)
            }
        }

        override fun onClose(client: FoxSocketClient) {
            invokeCallback {
                it.onClose(client)
            }
        }

        override fun onError(client: FoxSocketClient, e: Exception?) {
            invokeCallback {
                it.onError(client, e)
            }
            client.removeCallback(this)
        }

        /**
         * 调用所有的回调
         */
        private fun invokeCallback(block: (callback: Callback) -> Unit) {
            for (c in callbacks)
                block.invoke(c)
        }
    }
}