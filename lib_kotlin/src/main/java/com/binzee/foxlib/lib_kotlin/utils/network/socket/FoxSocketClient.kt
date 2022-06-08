package com.binzee.foxlib.lib_kotlin.utils.network.socket

import android.util.Log
import com.binzee.foxlib.lib_kotlin.utils.async.ThreadPoolUtil
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket
import java.util.*
import kotlin.collections.HashSet

/**
 * FoxSocketClient
 *
 * 简易Socket客户端包装类
 * @since  2022/6/8 11:20
 * @author Administrator
 *
 * @param socket    包装内容，socket客户端实例
 */
@Suppress("MemberVisibilityCanBePrivate")
class FoxSocketClient(
    val socket: Socket, callbacks: Collection<Callback>
) {
    companion object {
        private const val TAG = "FoxSocketClient"
    }

    /**
     * 客户端回调，观察者模式
     */
    interface Callback {

        /**
         * socket开启回调
         */
        fun onOpen(client: FoxSocketClient)

        /**
         * 消息回调
         */
        fun onMessage(client: FoxSocketClient, message: String)

        /**
         * socket关闭回调
         */
        fun onClose(client: FoxSocketClient)

        /**
         * socket异常回调
         */
        fun onError(client: FoxSocketClient, e: Exception?)
    }

    @Volatile
    var isRunning = true
        private set(value) {
            field = value
            if (!value) {
                if (!socket.isClosed) socket.close()
                executor.dispose()
                invokeCallback {
                    it.onClose(this)
                }
            }
        }

    // 读取线程和存活确认线程
    private val executor = ThreadPoolUtil.by(2, 2)
    // 回调列表
    private val callbackSet = Collections.synchronizedSet(HashSet<Callback>())

    init {
        callbackSet.addAll(callbacks)

        // socket开启
        invokeCallback {
            it.onOpen(this)
        }

        // 循环确认当前socket是否存活
        executor.execute {
            try {
                var run = true

                // 循环任务
                runInLoop({run}) {
                    if (!socket.isConnected)
                        run = false
                    Thread.sleep(2000)
                }
            } catch (e: Exception) {
                // 循环异常
                invokeCallback {
                    it.onError(this, e)
                }
            } finally {
                invokeCallback {
                    it.onClose(this)
                }
                isRunning = false
            }
        }
        // 循环获取socket消息
        executor.execute {
            try {
                runInLoop({isRunning}) {
                    val reader =
                        BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
                    val message = reader.readLine()
                    if (message == null) {
                        isRunning = false
                    }
                    invokeCallback {
                        it.onMessage(this, message)
                    }
                }
            } catch (e: Exception) {
                invokeCallback {
                    it.onError(this, e)
                }
            } finally {
                if (isRunning) isRunning = false
            }
        }
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
     * 发送消息
     */
    fun send(message: String) {
        try {
            val writer =
                PrintWriter(OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8), true)
            writer.println(message)
        } catch (e: Exception) {
            invokeCallback {
                it.onError(this, e)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 调用所有回调的方法
     */
    private fun invokeCallback(block: (callback: Callback) -> Unit) {
        for (c in callbackSet) block.invoke(c)
    }

    /**
     * 自动捕获中断异常的循环
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
}