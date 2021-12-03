package com.binzee.foxlib.lib_kotlin.utils.alert

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.binzee.foxlib.lib_kotlin.R
import com.binzee.foxlib.lib_kotlin.utils.resource.DimenUtil
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * snackbar工具
 *
 * @author tong.xw
 * 2021/12/03 17:00
 */
object SnackbarUtil {

    data class ActionData(
        val text: String,
        val callback: Runnable? = null
    )

    fun createCustom(
        archView: View,
        duration: Int,
        snackbarView: View
    ): Snackbar = createCustom(archView.context, archView, duration, snackbarView)

    fun createCustom(
        archView: View,
        message: CharSequence,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_LONG,
        action: ActionData? = null,
        @DrawableRes iconRes: Int = -1
    ): Snackbar = createCustom(archView.context, archView, message, duration, action, iconRes)

    /**
     * 生成一个自定义Snackbar
     *
     * @param ctx   上下文实例
     * @param archView  锚点View
     * @param duration  显示时长
     * @param snackbarView  snackbar实例
     */
    fun createCustom(
        ctx: Context,
        archView: View,
        duration: Int,
        snackbarView: View
    ): Snackbar {
        return Snackbar.make(ctx, archView, "", duration).also {
            it.view.findViewById<TextView>(R.id.snackbar_text).visibility = View.GONE
            (it.view as ViewGroup).addView(snackbarView, 0)
        }
    }

    /**
     * 生成一个自定义Snackbar
     *
     * @param ctx   上下文实例
     * @param archView  锚点View
     * @param message   显示信息
     * @param iconRes   最左侧的图标，默认没有
     * @param duration  显示时长，默认长
     * @param action    snackbar的按钮，若为空则无按钮
     */
    fun createCustom(
        ctx: Context,
        archView: View,
        message: CharSequence,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_LONG,
        action: ActionData? = null,
        @DrawableRes iconRes: Int = -1
    ): Snackbar = Snackbar.make(ctx, archView, message, duration).also {
        if (iconRes != -1) {
            // 图标
            val dUtil = DimenUtil()
            val textView = it.view.findViewById<TextView>(R.id.snackbar_text)
            val d = ResourcesCompat.getDrawable(ctx.resources, iconRes, null)
            d?.also { drawable ->
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                textView.setCompoundDrawables(d, null, null, null)
                textView.compoundDrawablePadding = dUtil.dp2Px(6F).toInt()
                textView.gravity = Gravity.CENTER
            }
        }

        if (action != null) {
            // 按钮
            it.setAction(action.text) {
                action.callback
            }
        }
    }
}