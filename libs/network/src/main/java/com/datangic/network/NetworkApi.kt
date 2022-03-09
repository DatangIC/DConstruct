package com.datangic.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.datangic.network.impl.*
import com.datangic.network.interceptor.RequestInterceptor
import com.datangic.network.interceptor.ResponseInterceptor
import com.datangic.network.livedata.LiveData2CallAdapterFactory
import com.datangic.network.networkState.NetworkState
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object NetworkApi {
    private lateinit var context: Context
    private var requestInfo: IRequestInfo? = null
    private var cacheFile: File? = null
    private var cookieJar: CookieJar? = null
    internal var logger: ILogger? = null

    //    private var authorizationInfo: IAuthorization? = null
    private val mHandler = Handler(Looper.getMainLooper())
    fun init(
        context: Context,
        infoI: IRequestInfo? = DefaultRequestInfo(),
        logger: ILogger? = null,
        cookieJar: CookieJar? = null,
    ): NetworkApi {
        this.requestInfo = infoI
        this.logger = logger
        this.context = context
        this.cacheFile = File(context.cacheDir, "http-cache")
        NetworkState.register(context)
        SharePrefer.init(context)
        this.cookieJar = cookieJar ?: object : CookieJar {
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return SharePrefer.getCookies()
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                SharePrefer.saveCookies(url.topPrivateDomain(), cookies)
            }
        }
        return this
    }

    fun openLog(logger: ILogger? = null): NetworkApi {
        this.logger = logger ?: DefaultLogger()
        return this
    }

    fun setRequestInfo(requestInfo: IRequestInfo): NetworkApi {
        this.requestInfo = requestInfo
        return this
    }

    /**
     * @param baseUrl 基础URL
     * @param withAuth 是否需要添加Auth
     * @param isCookie 是否启动cookie
     * @param interceptors 拦截器
     */
    inline fun <reified T> create(
        baseUrl: String,
        authorizationInfo: IAuthorization? = null,
        isCookie: Boolean = true,
        vararg interceptors: Interceptor
    ): T {
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            client(getOkHttpClient(isCookie, authorizationInfo, *interceptors))
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            addCallAdapterFactory(LiveData2CallAdapterFactory())
        }.build().create(T::class.java)
    }

    /**
     * @hide
     */
    fun getOkHttpClient(
        isCookie: Boolean,
        authorizationInfo: IAuthorization? = null,
        vararg interceptors: Interceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.apply {
            addInterceptor(ResponseInterceptor(context))
            var hasRequestInterceptor = false
            interceptors.forEach { interceptor ->
                if (interceptor is RequestInterceptor) hasRequestInterceptor = true
                addInterceptor(interceptor)
            }
            logger?.log(" Create API", "mInterceptor.size =${interceptors.size}")
            addInterceptor(RequestInterceptor(requestInfo, authorizationInfo))
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
        }
        requestInfo?.let {
            if (it.isDebug()) {
                okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        cacheFile?.let { file ->
            okHttpClientBuilder.cache(Cache(file, 1024 * 1024 * 17))
        }
        if (isCookie) {
            cookieJar?.let { okHttpClientBuilder.cookieJar(it) }
        }
        return okHttpClientBuilder.build()
    }

    /**
     * Be sure execute in main thread.
     *
     * @param runnable code
     */
    internal fun runInMainThread(runnable: Runnable) {
        if (Looper.getMainLooper().thread !== Thread.currentThread()) {
            mHandler.post(runnable)
        } else {
            runnable.run()
        }
    }

}