package com.datangic.api

import android.util.Log
import androidx.annotation.MainThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.datangic.api.login.LoginApi
import com.datangic.api.login.VerifyCodeResult
import com.datangic.network.AppExecutors
import com.datangic.network.NetworkApi
import com.datangic.network.ResponseState
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidLoginApiTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        NetworkApi.init(appContext)
        LoginApi.create().getAuthCode("1233434", LoginApi.LoginType.RegisterORLogin.value).map { response2observable<VerifyCodeResult>(it) }
            .subscribe {
                object : Observer<ResponseState<VerifyCodeResult>> {
                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                        Log.e("useAppContext", "onSubscribe")
                    }

                    override fun onNext(t: ResponseState<VerifyCodeResult>) {
                        println("onNext")
                        Log.e("useAppContext", "onNext")
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                        Log.e("useAppContext", "onError")
                    }

                    override fun onComplete() {
                        println("onComplete")
                        Log.e("useAppContext", "onComplete")
                    }
                }

                val value = LoginApi.create().getAuthCode2Live("1233434", LoginApi.LoginType.RegisterORLogin.value).value
                Thread.sleep(400)

                println(value)
                println(value.toString())
                AssertionError("hello world");

            }
    }
}