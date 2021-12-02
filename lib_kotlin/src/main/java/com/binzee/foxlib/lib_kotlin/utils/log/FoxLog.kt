@file:Suppress("MemberVisibilityCanBePrivate")

package com.binzee.foxlib.lib_kotlin.utils.log

/**
 * 日志类
 *
 * @author tong.xw
 * 2021/11/30 10:11
 */
object FoxLog {

    /**
     * 日志等级
     */
    enum class Level(val tag: String) {
        Verbose("V"),
        Debug("D"),
        Info("D"),
        Warn("W"),
        Error("E")
    }

    /**
     * 日志接口
     */
    interface Logger {

        /**
         * 输出日志
         *
         * @param level 等级
         * @param tag   标签
         * @param msg   信息
         * @param e     错误
         */
        fun log(level: Level, tag: String, msg: String, e: Throwable?)
    }

    // 日志输出工具
    private var mLogger: Logger? = null
    // 最低输出等级，默认为V
    private var mLevel = Level.Verbose

    /**
     * 设置输出工具和最低等级
     */
    fun setLog(logger: Logger, minLevel: Level = Level.Verbose) {
        mLogger = logger
        setMinLevel(minLevel)
    }

    /**
     * 设置最低等级
     */
    fun setMinLevel(level: Level) {
        mLevel = level
    }

    /**
     * 输出v
     */
    fun v(tag: String, msg: String = "", e: Throwable? = null) {
        val level = Level.Verbose
        if (shouldSkipLog(level)) return
        mLogger?.log(level, tag, msg, e)
    }

    /**
     * 输出d
     */
    fun d(tag: String, msg: String = "", e: Throwable? = null) {
        val level = Level.Debug
        if (shouldSkipLog(level)) return
        mLogger?.log(level, tag, msg, e)
    }

    /**
     * 输出i
     */
    fun i(tag: String, msg: String = "", e: Throwable? = null) {
        val level = Level.Info
        if (shouldSkipLog(level)) return
        mLogger?.log(level, tag, msg, e)
    }

    /**
     * 输出w
     */
    fun w(tag: String, msg: String = "", e: Throwable? = null) {
        val level = Level.Warn
        if (shouldSkipLog(level)) return
        mLogger?.log(level, tag, msg, e)
    }

    /**
     * 输出e
     */
    fun e(tag: String, msg: String = "", e: Throwable? = null) {
        val level = Level.Error
        if (shouldSkipLog(level)) return
        mLogger?.log(level, tag, msg, e)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 检查是否低于最低输出等级
     *
     * @return 若低于最低输出等级，则返回true
     */
    private fun shouldSkipLog(level: Level): Boolean {
        return level.ordinal >= mLevel.ordinal
    }
}