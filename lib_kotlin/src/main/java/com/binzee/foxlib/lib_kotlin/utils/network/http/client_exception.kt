package com.binzee.foxlib.lib_kotlin.utils.network.http

import java.io.IOException

/**
 * 异常基类
 */
abstract class ClientException: IOException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}

/**
 * 未知异常
 */
class UnknownClientException(e: Throwable): ClientException(e)