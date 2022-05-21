package com.binzee.foxlib.lib_kotlin.utils.async

import java.util.concurrent.*

/**
 * IAsyncUtil.kt
 *
 * 异步操作工具抽象
 * TODO 协程工具类
 * @since  2022/5/20 17:21
 * @author tong.xiwen
 */
interface IAsyncUtil {

    /**
     * 异步工作
     */
    fun execute(task: () -> Unit)

    /**
     * 获取future
     */
    fun <T> future(task: () -> T): Future<T>

    fun dispose()
}

object AsyncUtilFactory {
    val threadPoolUtil = ThreadPoolUtil
}

object ThreadPoolUtil {
    private var defaultCachePool: IAsyncUtil? = null

    /**
     * 给定线程池
     */
    fun byGivenPool(executor: ExecutorService): IAsyncUtil {
        return ThreadUtil(executor)
    }

    /**
     * 指定信息
     */
    fun by(
        fixCount: Int,
        maxCount: Int,
        keepAliveTime: Long = 60L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        queue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    ): IAsyncUtil {
        val executor = ThreadPoolExecutor(
            fixCount, maxCount,
            keepAliveTime, timeUnit,
            queue
        )
        return ThreadUtil(executor)
    }

    /**
     * 默认线程池
     */
    fun byDefault(): IAsyncUtil {
        return defaultCachePool ?: ThreadUtil(Executors.newCachedThreadPool()).also {
            defaultCachePool = it
        }
    }

    /**
     * 单线程线程池
     */
    fun bySingleThread(): IAsyncUtil {
        val executor = Executors.newCachedThreadPool()
        return ThreadUtil(executor)
    }


    /**
     * ThreadUtil
     *
     * 线程异步工具
     * @since  2022/5/20 17:25
     * @author tong.xiwen
     */
    private class ThreadUtil(private val executor: ExecutorService) : IAsyncUtil {

        override fun execute(task: () -> Unit) {
            executor.execute(task)
        }

        override fun <T> future(task: () -> T): Future<T> {
            return executor.submit(task)
        }

        override fun dispose() {
            executor.shutdown()
        }
    }
}
