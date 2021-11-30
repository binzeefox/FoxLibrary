@file:Suppress("MemberVisibilityCanBePrivate")

package com.binzee.foxlib.lib_kotlin.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * app包工具
 *
 * @author tong.xw
 * 2021/11/30 10:42
 */
object PackageUtil {

    /**
     * 获取包名
     */
    fun getPackageName(ctx: Context): String = ctx.packageName

    /**
     * 获取包管理器
     */
    fun getPackageManager(ctx: Context): PackageManager = ctx.packageManager

    /**
     * 获取包信息
     */
    fun getPackageInfo(ctx: Context): PackageInfo = getPackageManager(ctx).getPackageInfo(
        getPackageName(ctx), 0)

    /**
     * 获取app版本名
     */
    fun getVersionName(ctx: Context): String = getPackageInfo(ctx).versionName

    /**
     * 获取app版本号
     */
    fun getVersionCode(ctx: Context): Long {
        val packageInfo = getPackageInfo(ctx)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            packageInfo.longVersionCode
        else packageInfo.versionCode.toLong()
    }
}