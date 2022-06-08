package com.binzee.foxlib.lib_kotlin.utils.resource.bitmap

import android.graphics.Bitmap

/**
 * abstracts
 *
 * 抽象
 * @since  2022/6/8 14:08
 * @author Administrator
 */


/**
 * 亮度色调饱和度调整器
 */
interface HSLTool {
    fun create(): Bitmap
    fun hue(hue: Float): HSLTool
    fun saturation(saturation: Float): HSLTool
    fun lum(lum: Float): HSLTool

    /**
     * 工厂类
     */
    fun interface Factory {
        fun create(srcBitmap: Bitmap): HSLTool
    }
}

/**
 * 位图大小控制器
 * TODO 完成实现
 */
interface SizeAdjuster {

    fun interface Factory {
        fun create(srcBitmap: Bitmap): SizeAdjuster
    }
}
