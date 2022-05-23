package com.binzee.foxlib.lib_kotlin

import android.content.Context
import android.content.SharedPreferences
import java.util.*

/**
 * 用于FoxCore的一些配置工具
 *
 * @author tong.xw
 * 2021/11/30 10:39
 */
internal object FoxConfigs {
    private const val CONFIG_FILE_NAME = "FOX_CONFIG_FILE_NAME"
    private const val KEY_LANGUAGE_TAG = "FOX_KEY_LANGUAGE_TAG"

    /**
     * 配置工具
     */
    private val sp: SharedPreferences
        get() = FoxCore.appContext.getSharedPreferences(CONFIG_FILE_NAME, Context.MODE_PRIVATE)

    /**
     * 读取语言Tag
     */
    fun readLanguageTag(): String? =
        sp.getString(KEY_LANGUAGE_TAG, Locale.getDefault().toLanguageTag())

    /**
     * 写入语言Tag
     */
    fun writeLanguageTag(languageTag: String) {
        sp.edit().putString(KEY_LANGUAGE_TAG, languageTag).apply()
    }
}