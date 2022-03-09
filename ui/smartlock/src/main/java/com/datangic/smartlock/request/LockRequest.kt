package com.datangic.smartlock.request

import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.datangic.api.smartlock.UpgradeRequest
import com.datangic.smartlock.BASE_URL
import com.datangic.smartlock.UPDATE
import com.datangic.smartlock.utils.Logger
import com.google.gson.Gson
import okhttp3.Call
import org.json.JSONObject
import java.io.File
import java.io.IOException

object LockRequest {
    fun getUpdateForVolley(updateBean: UpgradeRequest.UpdateRequestData, action: ((JSONObject) -> Unit)?, error: ((error: VolleyError) -> Unit)?): JsonObjectRequest {
        Logger.e("LockRequest", "POST Request")
        return JsonObjectRequest(
                Request.Method.POST,
                BASE_URL + UPDATE,
                JSONObject(Gson().toJson(updateBean)),
                { r -> action?.let { it(r) } },
                { e -> error?.let { it(e) } }
        )
    }

    fun getUpdateForHttp(updateBean: UpgradeRequest.UpdateRequestData, action: ((Call, JSONObject) -> Unit)? = null, error: ((Call, IOException) -> Unit)? = null): ApiHttp.JsonRequest {
        return ApiHttp.JsonRequest(
                ApiHttp.HttpMethod.POST,
                BASE_URL + UPDATE,
                Gson().toJson(updateBean),
                action,
                error
        )
    }

    fun downloadForHttp(uri: String, file: File, action: ((Int, Long, String) -> Unit)? = null, error: ((Call, IOException) -> Unit)? = null): ApiHttp.DownloadRequest {

        return ApiHttp.DownloadRequest(
                ApiHttp.HttpMethod.GET,
                BASE_URL + uri,
                file,
                action,
                error)
    }
}