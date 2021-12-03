package com.binzee.foxlibrary

import android.app.Application
import com.binzee.foxlib.lib_kotlin.utils.log.FileLog
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog

/**
 * 应用
 *
 * @author tong.xw
 * 2021/12/03 14:23
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FoxLog.setLog(FileLog())
    }
}