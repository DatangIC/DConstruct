package com.datangic.smartlock.request

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


object ApiHttp {

    var client = OkHttpClient()
    val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    enum class HttpMethod {
        POST, GET
    }


    fun enqueue(jsonRequest: JsonRequest) {
        val requestBody: RequestBody = jsonRequest.json.toRequestBody(JSON)
        val request: Request = Request.Builder()
                .post(requestBody)
                .url(jsonRequest.url)
                .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                jsonRequest.onFailure?.let {
                    it(call, e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                jsonRequest.onResponse?.let {
                    it(call, JSONObject(response.body?.string() ?: ""))
                }
            }
        })
    }

    fun enqueue(downloadRequest: DownloadRequest) {
        var mAlreadyDownLength = 0
        val request: Request = Request.Builder()
                .get()
                .url(downloadRequest.url)
                .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                downloadRequest.onFailure?.let {
                    it(call, e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val inp: InputStream = it.byteStream()
                    val fileOutputStream = FileOutputStream(downloadRequest.filePath)
                    try {
                        val bytes = ByteArray(2048)
                        var len: Int
                        while (inp.read(bytes).also { it1 -> len = it1 } != -1) {
                            mAlreadyDownLength += len
                            fileOutputStream.write(bytes, 0, len)
                            downloadRequest.onProgress?.let { it1 ->
                                it1(mAlreadyDownLength, it.contentLength(), downloadRequest.filePath.name)
                            }
                        }
                    } catch (e: IOException) {
                        downloadRequest.onFailure?.let {
                            it(call, e)
                        }
                    } finally {
                        fileOutputStream.close()
                        inp.close()
                    }
                }
            }
        })
    }

    data class JsonRequest(
            val method: HttpMethod,
            val url: String,
            val json: String,
            val onResponse: ((Call, JSONObject) -> Unit)? = null,
            val onFailure: ((Call, IOException) -> Unit)? = null,
    )

    data class DownloadRequest(
            val method: HttpMethod,
            val url: String,
            val filePath: File,
            val onProgress: ((Int, Long, String) -> Unit)? = null,
            val onFailure: ((Call, IOException) -> Unit)? = null,
    )
}