package com.datangic.network

data class ResponseStatus<out T>(val requestStatus: RequestStatus, val data: T?, val message: String, val resId: Int? = null) {
    companion object {
        fun <T> success(data: T? = null): ResponseStatus<T> {
            return ResponseStatus(RequestStatus.SUCCESS, data, "Success")
        }

        fun <T> error(msg: String, resId: Int? = null, data: T? = null): ResponseStatus<T> {
            return ResponseStatus(RequestStatus.ERROR, data, msg, resId)
        }

        fun <T> empty(msg: String, data: T? = null): ResponseStatus<T> {
            return ResponseStatus(RequestStatus.EMPTY, data, msg)
        }

        fun <T> next(msg: String, data: T? = null): ResponseStatus<T> {
            return ResponseStatus(RequestStatus.NEXT, data, msg)
        }

        fun <T> loading(data: T? = null): ResponseStatus<T> {
            return ResponseStatus(RequestStatus.LOADING, data, "Loading")
        }
    }
}