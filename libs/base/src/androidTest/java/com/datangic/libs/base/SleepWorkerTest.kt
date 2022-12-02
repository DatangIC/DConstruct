package com.datangic.libs.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.TestWorkerBuilder
import com.datangic.libs.base.work.SleepWorker
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class SleepWorkerTest {
    private lateinit var context: Context
    private lateinit var executor: Executor
    private lateinit var data: Data

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
        data = Data.Builder().put("STE", 12).build()
    }

    @Test
    fun testSleepWorker() {
        val worker = TestListenableWorkerBuilder<SleepWorker>(
            context = context,
            inputData = data
        ).build()
        MainScope().launch(Dispatchers.IO) {
            val result = worker.doWork()
            result.outputData
            assertThat(result, `is`(Result.success()))
            assertThat(result.outputData.getInt("set", 2), `is`(12))
        }
    }
}
