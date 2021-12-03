package com.binzee.foxlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.binzee.foxlib.lib_kotlin.utils.ViewTool
import com.binzee.foxlib.lib_kotlin.utils.alert.SnackbarUtil
import com.binzee.foxlib.lib_kotlin.utils.alert.ToastUtil
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.lang.RuntimeException
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn_btn).setOnClickListener {
            SnackbarUtil.createCustom(window.decorView, "狐族万岁", Snackbar.LENGTH_INDEFINITE, SnackbarUtil.ActionData("好")).show()
            index++
        }
    }

    /**
     * 通过硬件获取mac
     */
    private fun getMacFromHardware(): String? {
        return try {
            val list = NetworkInterface.getNetworkInterfaces()
            for (ni in list) {
                if (ni.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = ni.hardwareAddress ?: return null
                val sb = StringBuilder()
                for (b in macBytes) {
                    sb.append(String.format("%02X:", b))
                }
                if (sb.isNotEmpty())
                    sb.deleteCharAt(sb.length - 1)
                return sb.toString()
            }
            null
        } catch (e: Exception) {
            Log.e("asd", "7.0以上获取mac异常", e)
            null
        }
    }
}