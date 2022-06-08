package com.binzee.foxlib.lib_kotlin.utils.resource.bitmap.impl

import android.graphics.*
import com.binzee.foxlib.lib_kotlin.utils.resource.bitmap.HSLTool

/**
 * DefaultHSLTool
 *
 * 位图HLS工具
 * @since  2022/6/8 14:04
 * @author Administrator
 */
class DefaultHSLToolImpl(private val srcBitmap: Bitmap): HSLTool {
    private val mMatrix = ColorMatrix()

    /**
     * 创建成品
     */
    override fun create(): Bitmap {
        val temp = Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, srcBitmap.config)
        val canvas = Canvas(temp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(mMatrix)
        }
        canvas.drawBitmap(srcBitmap, 0f, 0f, paint)
        return temp
    }

    /**
     * 色调
     */
    override fun hue(hue: Float): DefaultHSLToolImpl {
        val matrix = ColorMatrix()
        matrix.setRotate(0, hue)
        matrix.setRotate(1, hue)
        matrix.setRotate(2, hue)

        mMatrix.postConcat(matrix)
        return this
    }

    /**
     * 饱和度
     */
    override fun saturation(saturation: Float): DefaultHSLToolImpl {
        val matrix = ColorMatrix()
        matrix.setSaturation(saturation)
        mMatrix.postConcat(matrix)
        return this
    }

    /**
     * 亮度
     */
    override fun lum(lum: Float): DefaultHSLToolImpl {
        val matrix = ColorMatrix()
        matrix.setScale(lum, lum, lum, 1f)
        mMatrix.postConcat(matrix)
        return this
    }
}