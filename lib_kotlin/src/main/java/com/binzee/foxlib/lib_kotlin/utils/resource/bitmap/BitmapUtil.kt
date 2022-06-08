package com.binzee.foxlib.lib_kotlin.utils.resource.bitmap

import android.graphics.Bitmap
import com.binzee.foxlib.lib_kotlin.utils.resource.bitmap.impl.DefaultHSLToolImpl
import com.binzee.foxlib.lib_kotlin.utils.resource.bitmap.impl.DefaultSizeAdjusterImpl

/**
 * BitmapUtil
 *
 * 位图工具类
 * @since  2022/6/8 14:02
 * @author Administrator
 */
object BitmapUtil {

    /**
     * 色值调整器工厂
     */
    var hslToolFactory: HSLTool.Factory =
        HSLTool.Factory { srcBitmap -> DefaultHSLToolImpl(srcBitmap) }

    /**
     * 图片压缩工具
     */
    var sizeAdjusterFactory: SizeAdjuster.Factory =
        SizeAdjuster.Factory { srcBitmap -> DefaultSizeAdjusterImpl(srcBitmap) }

    /**
     * 图标亮度色调饱和度调整器
     */
    fun hsl(srcBitmap: Bitmap): HSLTool = hslToolFactory.create(srcBitmap)

    /**
     * 图片压缩工具
     */
    fun adjustSize(srcBitmap: Bitmap): SizeAdjuster = sizeAdjusterFactory.create(srcBitmap)
}