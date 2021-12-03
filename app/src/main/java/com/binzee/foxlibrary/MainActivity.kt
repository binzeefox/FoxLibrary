package com.binzee.foxlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog
import java.lang.Exception
import java.lang.RuntimeException
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FoxLog.d("BINZEE_FOX", "onCreate fox: ${getMacFromHardware()}")
        Log.d("BINZEE_FOX", "onCreate: ${getMacFromHardware()}")
        FoxLog.d("测试Tag", "onCreate fox: aaaaaaa", RuntimeException("啥都不说"))
        Log.d("测试Tag", "onCreate: aaaaaaa", RuntimeException("啥都不说"))
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