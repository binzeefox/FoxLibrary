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
    val simulatedBackStack = SimulatedActivityStack()
    // 资源文件
    val resources: Resources
        get() {
            return appContext.resources
        }
    // 版本名
    val versionName: String get() = PackageUtil.getVersionName(appContext)
    // 版本号
    val versionCode: Long get() = PackageUtil.getVersionCode(appContext)

    private val mMainHandler = Handler(Looper.getMainLooper())

    /**
     * 初始化
     */
    internal fun init(ctx: Context) {
        // 初始化应用context
        appContext = ctx.applicationContext
        // 注册全局日志收集
        registerActivityCallback()

        // 多语言设置
        FoxConfigs.readLanguageTag()?.apply {
            val locale = Locale.forLanguageTag(this)
            setLocale(locale, false)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置语言
     *
     * @param locale    区域设置
     * @param localize  是否本地化该设置，以便下次开启app自动适配
     * FIXME DEPRECATED
     */
    fun setLocale(locale: Locale, localize: Boolean) {
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration

        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, displayMetrics)
        if (localize) FoxConfigs.writeLanguageTag(locale.toLanguageTag())
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
        (appContext as Application).registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
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

        fun peek(): Activity = activityStack.peek()

        fun pop() = activityStack.pop()

        fun push(activity: Activity) = activityStack.push(activity)
    }
}