package com.binzee.foxlib.lib_kotlin.ui

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

/**
 * 活动基类
 *
 * @author tong.xw
 * 2021/11/30 10:55
 */
abstract class FoxActivity: AppCompatActivity() {

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
}