package com.binzee.foxlib.lib_kotlin.utils.network.http

/**
 * ClientUtil
 *
 * 网络请求工具
 * @since  2022/5/24 14:36
 * @author tong.xiwen
 */
object ClientUtil {
    var client: Client? = null
        private set
        get() {
            if (field == null) throw IllegalAccessException("call initClient() first !!!")
            return field
        }

    fun initClient(client: Client) {
        ClientUtil.client = client
    }

    fun request(options: Client.Options, callback: Client.Callback) {
        client!!.request(options, callback)
    }

    fun request(options: Client.Options): String {
        return client!!.request(options)
    }
}