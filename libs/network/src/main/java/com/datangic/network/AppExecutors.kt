package com.datangic.network

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class AppExecutors(
    private val diskIO: ExecutorService = Executors.newSingleThreadExecutor(),
    private val networkIO: ExecutorService = Executors.newFixedThreadPool(5),
    private val mainThread: Executor = MainThreadExecutor()
) {

    fun diskIO(): ExecutorService {
        return diskIO
    }

    fun networkIO(): ExecutorService {
        return networkIO
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