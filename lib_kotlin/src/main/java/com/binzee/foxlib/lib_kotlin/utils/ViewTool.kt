package com.binzee.foxlib.lib_kotlin.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import com.binzee.foxlib.lib_kotlin.FoxCore
import com.binzee.foxlib.lib_kotlin.R
import java.util.*

/**
 * View基础帮助类
 *
 * @param targetView    目标视图
 * @author tong.xw
 * 2021/12/03 15:12
 */
class ViewTool(
    val targetView: View
) {
    companion object {
        private const val DEFAULT_DEBOUNCE_DURATION = 500L   //默认防抖间隔(毫秒)
    }

    /**
     * 设置防抖点击事件
     *
     * @param listener  点击事件
     * @param duration  防抖无响应时间
     */
    fun setDebounceOnClickListener(listener: View.OnClickListener, duration: Long = DEFAULT_DEBOUNCE_DURATION) {
        targetView.setOnClickListener(object : View.OnClickListener {
            private val skip = duration

            override fun onClick(v: View?) {
                v ?: return
                val curTimestamp = System.currentTimeMillis()
                var tagTimestamp = -1L
                v.getTag(R.id.fox_view_util_debounce_id)?.also {
                    tagTimestamp = it as Long
                }
                if (tagTimestamp != -1L || tagTimestamp + skip < curTimestamp) {
                    // 第一次点击
                    v.setTag(R.id.fox_view_util_debounce_id, curTimestamp)
                    listener.onClick(v)
                }
            }
        })
    }

    /**
     * 获取View的截图Bitmap
     */
    fun captureViewShot(): Bitmap {
        val bitmap = Bitmap.createBitmap(targetView.width, targetView.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        targetView.layout(targetView.left, targetView.top, targetView.right, targetView.bottom)
        val backDrawable = targetView.background
        if (backDrawable == null) canvas.drawColor(Color.TRANSPARENT)
        else backDrawable.draw(canvas)

        targetView.draw(canvas)
        return  bitmap
    }

    ///////////////////////////////////////////////////////////////////////////
    // 整活儿
    ///////////////////////////////////////////////////////////////////////////

    private var discoTimer: Timer? = null

    /**
     * 整活儿disco模式
     *
     * 注意！不可恢复
     * @param active    开关
     */
    fun setDiscoMode(active: Boolean) {
        if (active) {
            fun discoModeValueFnc(values: IntArray) {
                values[0] += values[1]
                if (values[0] > 255) {
                    values[0] = 255
                    values[1] *= -1
                }
                if (values[0] < 0) {
                    values[0] = 0
                    values[1] *= -1
                }
            }

            discoTimer?.cancel()
            discoTimer = Timer()
            discoTimer?.schedule(
                object : TimerTask() {
                    val r = intArrayOf(0, 1)
                    val g = intArrayOf(0, 10)
                    val b = intArrayOf(0, 20)

                    override fun run() {
                        discoModeValueFnc(r)
                        discoModeValueFnc(g)
                        discoModeValueFnc(b)

                        FoxCore.runOnUiThread {
                            val colorValue = Color.argb(255, r[0], g[0], b[0])
                            targetView.setBackgroundColor(colorValue)
                        }
                    }
                }, 5, 5
            )
        } else {
            discoTimer?.cancel()
            discoTimer = null
        }
    }
}