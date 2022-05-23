package com.binzee.foxlib.lib_kotlin.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.LocaleList
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.binzee.foxlib.lib_kotlin.FoxCore
import java.util.*

/**
 * 活动基类
 *
 * @author tong.xw
 * 2021/11/30 10:55
 */
abstract class FoxActivity : AppCompatActivity() {

    fun setFullScreen() {
        val window = window
        val decorView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = decorView.windowInsetsController
            controller!!.hide(WindowInsets.Type.navigationBars())
        } else {
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 修改语言
     */
    override fun attachBaseContext(newBase: Context?) {
        // 多语言设置
        FoxCore.setLocale(FoxCore.activeLocale)

        super.attachBaseContext(newBase?.createConfigurationContext(
            newBase.resources.configuration.apply {
                val locale = FoxCore.activeLocale

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setLocales(LocaleList(locale))
                } else {
                    setLocale(locale)
                }
                Locale.setDefault(locale)
            }
        ))
    }
}