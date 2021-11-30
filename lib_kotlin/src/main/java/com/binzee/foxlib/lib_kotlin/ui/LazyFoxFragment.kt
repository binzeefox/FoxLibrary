package com.binzee.foxlib.lib_kotlin.ui

import androidx.fragment.app.Fragment

/**
 * 懒加载碎片基类
 *
 * @author tong.xw
 * 2021/11/30 10:57
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class LazyFoxFragment : Fragment() {
    var isLoaded = false
    protected val shouldOnLoad: Boolean = isVisible && !isLoaded

    override fun onResume() {
        super.onResume()
        if (shouldOnLoad)  onLazyLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        isLoaded = false
    }

    /**
     * 懒加载回调
     */
    open fun onLazyLoad() {
        this.isLoaded = true
    }
}