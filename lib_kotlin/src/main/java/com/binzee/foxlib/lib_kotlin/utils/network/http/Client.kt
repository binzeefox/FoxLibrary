package com.binzee.foxlib.lib_kotlin.utils.network.http

/**
 * Client
 *
 * 网络框架
 * @since  2022/5/24 14:08
 * @author tong.xiwen
 * 
 * @property Method 方法枚举
 * @property Callback   请求回调
 * @property Options    请求参数
 * @property readTimeout    读取超时
 * @property connectTimeout 连接超时
 * @property baseUrl    基础路径
 */
interface Client {

    /**
     * 方法枚举
     */
    enum class Method(val methodLabel: String) {
        GET("get"),
        POST("POST")
    }
    
    /**
     * 请求回调
     */
    interface Callback {
        fun onStart() {}
        fun onSuccess(content: String)
        fun onError(e: Throwable)
        fun onComplete()
    }
    
    /**
     * 请求参数
     * 
     * @property parameters 请求参数
     * @property headers    请求头
     * @property method 请求方法
     * @property api    请求路径
     * @property differ 用于区分hash值的
     */
    data class Options (
        val parameters: MutableMap<String, String?> = hashMapOf(),
        val headers: MutableMap<String, String> = hashMapOf(),
        val method: Method = Method.GET,
        val api: String = "",
        private val differ: Long = 0
    )
    
    val readTimeout: Long
    val connectTimeout: Long
    val baseUrl: String
    
    /**
     * 异步请求
     *
     * @return key，用来取消请求的
     */
    fun request(options: Options, callback: Callback): String
    
    /**
     * 请求
     *
     * @return 请求响应的字符串
     */
    fun request(options: Options) : String

    /**
     * 取消请求
     */
    fun cancel(key: String)

    fun cancelAll()
}