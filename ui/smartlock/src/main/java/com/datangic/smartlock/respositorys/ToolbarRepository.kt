package com.datangic.smartlock.respositorys

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.datangic.smartlock.R
import com.datangic.smartlock.components.ToolbarProgress

open class ToolbarRepository {
    /**
     * mOnBackPressed 函数表示在退出之前需要做的事情，返回true表示可以退出，返回false表示不让退出
     */
    var mOnBackPressed: (() -> Boolean)? = null

    val mScannerActivityToolBar by lazy { ToolbarProgress(R.string.title_scanning) }

    val mSystemActivityToolBar by lazy { ToolbarProgress(R.string.title_setting) }

    fun initToolbarWithBack(activity: AppCompatActivity, toolbar: Toolbar) {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }
    }
}