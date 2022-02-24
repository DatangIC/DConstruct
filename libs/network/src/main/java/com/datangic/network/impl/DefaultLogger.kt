package com.datangic.network.impl

import android.util.Log

class DefaultLogger : ILogger {
    override fun log(tag: String?, msg: String) {
        Log.i(tag, msg)
    }
}