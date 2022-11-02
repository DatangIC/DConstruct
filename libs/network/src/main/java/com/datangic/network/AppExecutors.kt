package com.datangic.network

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

open class AppExecutors(
    private val singleIO: ExecutorService = Executors.newSingleThreadExecutor(),
    private val fixedIO: ExecutorService = Executors.newFixedThreadPool(5),
    private val mainThread: Executor = MainThreadExecutor(),
) {

    fun singleIO(): ExecutorService {
        return singleIO
    }

    fun fixedIO(): ExecutorService {
        return fixedIO
    }

    fun scopeIo(context: CoroutineContext): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}