@file:Suppress("MemberVisibilityCanBePrivate")

package com.binzee.foxlib.lib_kotlin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.binzee.foxlib.lib_kotlin.utils.PackageUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * 核心类
 *
 * @author tong.xw
 * 2021/11/10 10:16
 */
object FoxCore {
    // 应用Context
    lateinit var appContext: Context

    // 模拟返回栈
    internal val simulatedBackStack = SimulatedActivityStack()

    // 资源文件
    val resources: Resources
        get() {
            return appContext.resources
        }

    // 版本名
    val versionName: String get() = PackageUtil.getVersionName(appContext)

    // 版本号
    val versionCode: Long get() = PackageUtil.getVersionCode(appContext)

    // 激活的Locale
    val activeLocale: Locale
        get() {
            val tag = FoxConfigs.readLanguageTag() ?: return Locale.getDefault()
            return Locale.forLanguageTag(tag)
        }

    private val mMainHandler = Handler(Looper.getMainLooper())

    /**
     * 初始化
     */
    internal fun init(ctx: Context) {
        // 初始化应用context
        appContext = ctx.applicationContext
        // 注册全局日志收集
        registerActivityCallback()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置语言
     *
     * 需要重启Activity，并重写attachBaseContext
     * @see com.binzee.foxlib.lib_kotlin.ui.FoxActivity
     * @param locale    区域设置
     */
    fun setLocale(locale: Locale) {
        if (Locale.getDefault() != locale)
            Locale.setDefault(locale)
        FoxConfigs.writeLanguageTag(locale.toLanguageTag())
    }

    /**
     * 主线程运行
     */
    fun runOnUiThread(runnable: Runnable) {
        mMainHandler.post(runnable)
    }

    /**
     * 主线程运行
     */
    fun runOnUiThread(block: () -> Unit) {
        mMainHandler.post {
            block.invoke()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 注册Activity回调
     */
    private fun registerActivityCallback() {
        (appContext as Application).registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                // 入栈
                simulatedBackStack.push(p0)
            }

            override fun onActivityResumed(p0: Activity) {
                // 状态可见，转入栈顶
                simulatedBackStack.moveToTop(p0)
            }

            override fun onActivityDestroyed(p0: Activity) {
                // 回收
                simulatedBackStack.removeAndKill(p0)
            }


            override fun onActivityPaused(p0: Activity) {
                // do nothing...
            }

            override fun onActivityStopped(p0: Activity) {
                // do nothing...
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                // do nothing...
            }

            override fun onActivityStarted(p0: Activity) {
                // do nothing...
            }
        })
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    class SimulatedActivityStack {
        private val activityStack = Stack<Activity>()

        internal fun moveToTop(activity: Activity) {
            if (activityStack.remove(activity))
                activityStack.push(activity)
        }

        fun removeAndKill(activity: Activity) {
            if (!activity.isFinishing && !activity.isDestroyed)
                activity.finish()
            activityStack.remove(activity)
        }

        fun snapshot(): List<Activity> = ArrayList(activityStack)

        fun peek(): Activity? = activityStack.peek()

        fun pop(): Activity? = activityStack.pop()

        fun push(activity: Activity) = activityStack.push(activity)
    }
}