package com.binzee.foxlib.lib_kotlin.utils.log

import android.util.Log

/**
 * 默认Logger
 *
 * @author tong.xw
 * 2021/12/03 15:05
 */
class DefaultLogger : FoxLog.Logger {

    override fun log(level: FoxLog.Level, tag: String, msg: String, e: Throwable?) {
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
    }
}