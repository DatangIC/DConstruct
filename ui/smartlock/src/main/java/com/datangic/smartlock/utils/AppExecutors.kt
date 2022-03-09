package com.datangic.smartlock.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


open class AppExecutors(
    private val singleIO: ExecutorService = Executors.newSingleThreadExecutor(),
    private val fixedIO: ExecutorService = Executors.newFixedThreadPool(3),
    private val mainThread: Executor = MainThreadExecutor()
) {

    fun singleIO(): ExecutorService {
        return singleIO
    }

    fun fixedIO(): ExecutorService {
        return fixedIO
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