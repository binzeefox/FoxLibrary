package com.binzee.foxlib.lib_kotlin.utils.resource

import android.content.Context
import com.binzee.foxlib.lib_kotlin.FoxCore

/**
 * 维度工具
 *
 * @author tong.xw
 * 2021/12/03 17:15
 */
class DimenUtil(ctx: Context = FoxCore.appContext) {
    private val density: Float = ctx.resources.displayMetrics.density

    fun dp2Px(dp: Float): Float = dp * density + 0.5f

    fun px2Dp(px: Float): Float = px / density + 0.5f
}