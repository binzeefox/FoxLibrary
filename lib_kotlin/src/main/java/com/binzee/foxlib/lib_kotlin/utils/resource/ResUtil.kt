package com.binzee.foxlib.lib_kotlin.utils.resource

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.binzee.foxlib.lib_kotlin.FoxCore
import java.io.File

/**
 * ResUtil
 *
 * 资源文件工具
 * @since  2022/5/21 9:40
 * @author tong.xiwen
 */
object ResUtil {

    /**
     * 获取图片资源
     */
    fun getDrawable(@DrawableRes id: Int, theme: Resources.Theme? = null): Drawable? {
        return ResourcesCompat.getDrawable(FoxCore.resources, id, theme)
    }

    /**
     * 获取颜色
     */
    @ColorInt
    fun getColor(@ColorRes id: Int, theme: Resources.Theme? = null): Int {
        return ResourcesCompat.getColor(FoxCore.resources, id, theme)
    }

    /**
     * 获取字符串
     */
    fun getString(@StringRes id: Int, vararg formatArgs: Any = emptyArray()): String {
        return if (formatArgs.isNotEmpty()) {
            FoxCore.appContext.getString(id, formatArgs)
        } else {
            FoxCore.appContext.getString(id)
        }
    }

    /**
     * 获取数值
     */
    fun getDimen(@DimenRes id: Int): Float {
        return FoxCore.resources.getDimension(id)
    }
}