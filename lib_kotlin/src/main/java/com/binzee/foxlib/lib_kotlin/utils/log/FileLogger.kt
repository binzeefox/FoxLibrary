package com.binzee.foxlib.lib_kotlin.utils.log

import android.util.Log
import com.binzee.foxlib.lib_kotlin.FoxCore
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * 本地化log
 *
 * @param logDir    日志路径
 * @param flushDuration 刷新间隔
 * @author tong.xw
 * 2021/12/01 10:48
 */
class FileLogger(
    private val logDir: File? = getDefaultLogDir(),
    private val flushDuration: Long = DEFAULT_FLUSH_DURATION
) : FoxLog.Logger {
    companion object {
        private const val DEFAULT_FLUSH_DURATION = 30 * 1000L

        fun getDefaultLogDir(): File? {
            val dir = FoxCore.appContext.getExternalFilesDir("logs")
            return dir?.also {
                if (!it.exists()) it.mkdir()
            }
        }
    }

    private val date: String
        get() = SimpleDateFormat("yyyy-MM-dd", FoxCore.activeLocale).format(
            System.currentTimeMillis()
        )
    private val logFile: File get() = File(logDir, "log-$date.txt")

    private lateinit var loggerWriter: FileWriter
    private val loggerExecutor = Executors.newFixedThreadPool(2)    //日志本地化线程，刷新线程
    private val loggerLock = Any()  //log 监控器
    private val loggerQueue: LinkedBlockingQueue<String> = LinkedBlockingQueue()
    private val logTimer: Timer = Timer() //定时清空缓冲流

    /**
     * 是否已经关闭
     */
    @Volatile
    var closed = false
        private set

    /**
     * 是否需要刷新
     */
    @Volatile
    private var shouldFlush = false

    init {
        // 写入线程
        loggerExecutor.execute {
            val loggerFile = logFile
            logDir ?: throw LogDirNotSpecifiedException()
            loggerWriter = FileWriter(loggerFile)
            loggerWriter.use {
                try {
                    // 死循环，获取队列内容并写入
                    while (!closed) {
                        val line = loggerQueue.take()
                        synchronized(loggerLock) {
                            it.write(line)
                            shouldFlush = true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    it.flush()
                    close()
                }
            }
        }

        // 定时刷新
        logTimer.schedule(object : TimerTask() {
            override fun run() {
                if (shouldFlush) flush()
            }
        }, flushDuration, flushDuration)
    }

    override fun log(level: FoxLog.Level, tag: String, msg: String, e: Throwable?) {
        val timestamp = System.currentTimeMillis()

        when (level) {
            FoxLog.Level.Verbose -> {
                Log.v(tag, msg, e)
            }
            FoxLog.Level.Debug -> {
                Log.d(tag, msg, e)
            }
            FoxLog.Level.Info -> {
                Log.d(tag, msg, e)
            }
            FoxLog.Level.Warn -> {
                Log.w(tag, msg, e)
            }
            FoxLog.Level.Error -> {
                Log.w(tag, msg, e)
            }
        }
        val line = getLocalizeStringLine(timestamp, level, tag, msg, e)
        loggerQueue.add(line)
    }

    /**
     * 刷新缓冲流
     */
    fun flush() {
        loggerExecutor.execute {
            synchronized(loggerLock) {
                loggerWriter.flush()
                shouldFlush = false
            }
        }
    }

    /**
     * 结束
     */
    fun close() {
        closed = true
        loggerExecutor.shutdownNow()
        logTimer.cancel()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取用于本地化的行
     */
    private fun getLocalizeStringLine(
        timestamp: Long,
        level: FoxLog.Level,
        tag: String,
        msg: String,
        e: Throwable?
    ): String {
        val time =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS", FoxCore.activeLocale).format(timestamp)
        var line = "$time ${level.tag}/$tag: $msg"
        e?.also {
            line += ". throw -> ${e.stackTraceToString()}"
        }
        line += "\n"
        return line
    }

    ///////////////////////////////////////////////////////////////////////////
    // 异常
    ///////////////////////////////////////////////////////////////////////////

    class LogDirNotSpecifiedException : IllegalStateException("日志文件目录未指定")
}