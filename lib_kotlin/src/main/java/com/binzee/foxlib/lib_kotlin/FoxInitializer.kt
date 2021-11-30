package com.binzee.foxlib.lib_kotlin

import android.content.Context
import androidx.startup.Initializer

/**
 * StartUp初始化
 *
 * @author tong.xw
 * 2021/11/10 10:14
 */
class FoxInitializer: Initializer<FoxCore> {

    override fun create(context: Context): FoxCore {
        FoxCore.init(context)
        return FoxCore
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}