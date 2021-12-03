package com.binzee.foxlib.lib_kotlin.utils.lifecycle.permission

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.binzee.foxlib.lib_kotlin.FoxCore
import com.binzee.foxlib.lib_kotlin.utils.lifecycle.BaseLifecycleWatcher
import com.binzee.foxlib.lib_kotlin.utils.log.FoxLog

/**
 * 权限回调
 */
fun interface OnPermissionResultListener {

    /**
     * 权限获取回调
     *
     * @param requestCode 请求码
     * @param failedList  失败权限，包含不再询问的权限
     * @param noAskList   不再询问的权限
     */
    fun onResult(requestCode: Int, failedList: List<String>, noAskList: List<String>)
}

/**
 * 权限工具
 *
 * @author tong.xw
 * 2021/12/03 17:45
 */
class PermissionUtil(fm: FragmentManager): BaseLifecycleWatcher(fm), OnPermissionResultListener {
    companion object {
        fun with(activity: AppCompatActivity): PermissionUtil = PermissionUtil(activity.supportFragmentManager)
        fun with(fragment: Fragment): PermissionUtil = PermissionUtil(fragment.childFragmentManager)
        fun withTopActivity(): PermissionUtil = with(FoxCore.simulatedBackStack.peek() as AppCompatActivity)
    }

    private var resultListener: OnPermissionResultListener? = null
    private val permissionFragment: WatcherFragment get() = watcherFragment as WatcherFragment

    override fun createWatcherFragment(): Fragment {
        val fragment = WatcherFragment()
        fragment.listener = this
        return fragment
    }

    override fun onResult(requestCode: Int, failedList: List<String>, noAskList: List<String>) {
        resultListener?.onResult(requestCode, failedList, noAskList)
        resultListener = null
        permissionFragment.clearPermission()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 业务方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 添加权限
     */
    fun addPermission(permission: String): PermissionUtil {
        permissionFragment.addPermission(permission)
        return this
    }

    /**
     * 添加权限列表
     */
    fun addPermissions(permissions: List<String>): PermissionUtil {
        for (p in permissions) addPermission(p)
        return this
    }

    /**
     * 设置权限列表
     */
    fun setPermissions(permissions: List<String>): PermissionUtil {
        permissionFragment.clearPermission()
        addPermissions(permissions)
        return this
    }

    /**
     * 检查权限
     */
    fun check(requestCode: Int, listener: OnPermissionResultListener) {
        resultListener = listener
        permissionFragment.checkPermission(requestCode)
    }

    /**
     * 请求权限
     */
    fun request(requestCode: Int, listener: OnPermissionResultListener) {
        resultListener = listener
        permissionFragment.requestPermission(requestCode)
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 内部Fragment
     */
    class WatcherFragment: Fragment() {
        companion object {
            private const val TAG = "PermissionUtil_Fragment"
        }
        var listener: OnPermissionResultListener? = null

        // 任务列表
        private val permissionList: ArrayList<String> = arrayListOf()

        override fun onDestroy() {
            super.onDestroy()
            listener = null
        }

        /**
         * 添加权限
         */
        fun addPermission(permission: String) {
            permissionList.add(permission)
        }

        /**
         * 清空权限
         */
        fun clearPermission() {
            permissionList.clear()
        }

        /**
         * 仅检查权限
         */
        fun checkPermission(requestCode: Int) {
            FoxLog.d(TAG, "checkPermission: 检查权限 => $permissionList")
            val failedList = ArrayList<String>()
            val noAskList = ArrayList<String>()
            for (permission in permissionList) {
                requireContext().also {
                    val result = ActivityCompat.checkSelfPermission(it, permission)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        failedList.add(permission)
                        if (checkNoAsk(permission)) noAskList.add(permission)
                    }
                }
            }
            listener?.onResult(requestCode, failedList, noAskList)
        }

        /**
         * 检查并请求权限
         */
        fun requestPermission(requestCode: Int) {
            requestPermissions(permissionList.toTypedArray(), requestCode)
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            val failedList = ArrayList<String>()
            val noAskList = ArrayList<String>()
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    failedList.add(permissions[i])
                    if (checkNoAsk(permissions[i]))
                        noAskList.add(permissions[i])
                }
            }

            if (failedList.isNotEmpty())
                FoxLog.d(TAG, "onRequestPermissionsResult: 权限未通过 => $failedList")
            if (noAskList.isNotEmpty())
                FoxLog.d(TAG, "onRequestPermissionsResult: 权限不再询问 => $noAskList")
            listener?.onResult(requestCode, failedList, noAskList)
        }

        ///////////////////////////////////////////////////////////////////////////
        // 内部方法
        ///////////////////////////////////////////////////////////////////////////

        /**
         * 检查权限是否为不再询问
         */
        private fun checkNoAsk(permission: String): Boolean {
            return shouldShowRequestPermissionRationale(permission)
        }
    }
}