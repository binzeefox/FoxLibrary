package com.binzee.foxlib.lib_kotlin.utils.alert

import android.content.Context
import android.widget.Toast
import com.binzee.foxlib.lib_kotlin.FoxCore

/**
 * toast工具
 *
 * @author tong.xw
 * 2021/12/03 16:47
 */
object ToastUtil {
    private const val DEFAULT_DURATION = Toast.LENGTH_SHORT //默认toast时长

    // 上下文实例
    private val mCtx: Context
        get() = FoxCore.simulatedBackStack.peek() ?: FoxCore.appContext

    // Toast实例
    private var mToast: Toast? = null

    /**
     * 显示Toast
     */
    fun showToastNow(text: CharSequence, duration: Int = DEFAULT_DURATION) {
        mToast?.cancel()
        mToast = Toast.makeText(mCtx, text, duration)
        mToast?.show()
    }

    /**
     * 显示Toast
     */
    fun showToast(text: CharSequence, duration: Int = DEFAULT_DURATION) {
        mToast = Toast.makeText(mCtx, text, duration)
        mToast?.show()
    }

    fun showToastNow(toast: Toast) {
        mToast?.cancel()
        mToast = toast
        toast.show()
    }

    fun showToast(toast: Toast) {
        mToast = toast
        toast.show()
    }
}