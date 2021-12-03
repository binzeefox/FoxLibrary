package com.binzee.foxlib.lib_kotlin.utils.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 基于无页面Fragment的请求者
 *
 * @author tong.xw
 * 2021/12/03 17:32
 */
abstract class BaseLifecycleWatcher(
    private val fm: FragmentManager,
    private val fragmentTag: String = DEFAULT_FRAGMENT_TAG
) {
    companion object {
        private const val DEFAULT_FRAGMENT_TAG = "base_lifecycle_watcher_fragment_tag"
    }

    var watcherFragment: Fragment
        private set

    init {
        watcherFragment = fm.findFragmentByTag(fragmentTag) ?: createWatcherFragment().also {
            fm.beginTransaction()
                .add(it, fragmentTag)
                .commitNow()
        }
    }

    protected abstract fun createWatcherFragment(): Fragment
}