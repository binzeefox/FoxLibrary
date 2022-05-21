package com.binzee.foxlibrary

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.binzee.foxlib.lib_kotlin.utils.alert.SnackbarUtil
import com.binzee.foxlib.lib_kotlin.utils.async.AsyncUtilFactory
import com.binzee.foxlib.lib_kotlin.utils.permission.PermissionUtil
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        findViewById<View>(R.id.btn_btn).setOnClickListener {
//            SnackbarUtil.createCustom(
//                window.decorView,
//                "狐族万岁",
//                Snackbar.LENGTH_INDEFINITE,
//                SnackbarUtil.ActionData("好")
//            ).show()
//            index++
//        }

        requestPermission()
        asyncTest()
    }

    private fun requestPermission() {
        PermissionUtil
            .with(this)
            .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .request(0x01) { requestCode, failedList, noAskList ->
                if (failedList.isNotEmpty())
                    SnackbarUtil.createCustom(
                        findViewById(R.id.cl_main),
                        "获取权限失败",
                        Snackbar.LENGTH_INDEFINITE,
                        SnackbarUtil.ActionData("重试" ) {
                            requestPermission()
                        }
                    ).show()
            }
    }

    private fun asyncTest() {
        // 获取默认的线程工具
        val util = AsyncUtilFactory.threadPoolUtil.byDefault()
        util.execute {
            // 耗时操作
        }
        val future = util.future {
            // 耗时操作
        }
        future.get()
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