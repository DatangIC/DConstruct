package com.datangic.smartlock.request

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.datangic.smartlock.utils.Logger
import java.io.File


@SuppressLint("StaticFieldLeak")
object ApiVolley {

    val TAG = "APIJSON"


    // Instantiate the RequestQueue with the cache and network. Start the queue.
    var mRequestQueue: RequestQueue? = null

    private var mContext: Context? = null
    fun init(context: Context) {
        // Instantiate the cache
        mContext = context
        val cache = DiskBasedCache(File(context.filesDir.path.toString() + "/Volley/"), 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        mRequestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }

    fun download(uri: String, filename: String): Long {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(uri))
        //设置漫游条件下是否可以下载
        request.setAllowedOverRoaming(false)
        request.setVisibleInDownloadsUi(true)
        //设置文件存放路径
        Logger.e(TAG, "download0")
        mContext?.let {
            Logger.e(TAG, "download")
            val file = File(it.getExternalFilesDir("Software")?.path.toString() + "/", filename)
            request.setDestinationUri(Uri.fromFile(file))
            val downloadManager = mContext?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
            return downloadManager.enqueue(request)
        }
        return 0

    }

}