package com.datangic.api

import com.datangic.network.ApiObservable
import com.datangic.network.ResponseStatus
import com.google.gson.JsonElement
import io.reactivex.rxjava3.core.Observable
import retrofit2.HttpException


class LockApiObservable<JsonElement>(upstream: Observable<DataResponse<JsonElement>>) :
    ApiObservable<DataResponse<JsonElement>, JsonElement>(upstream) {
    override fun doNext(i: DataResponse<JsonElement>): ResponseStatus<JsonElement> {
        return ResponseStatus.success(i.content)
    }

    // 此处做错误代码处理
    override fun doError(e: HttpException): ResponseStatus<JsonElement> {
        return ResponseStatus.error(e.code().toString(), getSource(e.code().toString()))
    }
}
