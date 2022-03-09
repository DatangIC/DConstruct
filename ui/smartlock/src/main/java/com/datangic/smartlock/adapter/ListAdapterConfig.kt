package com.datangic.smartlock.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ListAdapterConfig {

    @SuppressLint("RestrictedApi")
    fun <T> getAsyncDifferConfig(diffCallback: DiffUtil.ItemCallback<T> = getDiffCallback<T>()) = AsyncDifferConfig
        .Builder(diffCallback)
        .setMainThreadExecutor(object : Executor {
            private val mainThreadHandler = Handler(Looper.getMainLooper())
            override fun execute(command: Runnable) {
                mainThreadHandler.post(command)
            }
        })
        .setBackgroundThreadExecutor(Executors.newFixedThreadPool(3))
        .build()

    private fun <T> getDiffCallback() = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }
}