package com.datangic.network

data class ResponseState<out T>(val requestStatus: RequestStatus, val data: T?, val message: String) {
    companion object {
        fun <T> success(data: T? = null): ResponseState<T> {
            return ResponseState(RequestStatus.SUCCESS, data, "Success")
        }

        fun <T> error(msg: String, data: T? = null): ResponseState<T> {
            return ResponseState(RequestStatus.ERROR, data, msg)
        }

        fun <T> empty(msg: String, data: T? = null): ResponseState<T> {
            return ResponseState(RequestStatus.EMPTY, data, msg)
        }

        fun <T> next(msg: String, data: T? = null): ResponseState<T> {
            return ResponseState(RequestStatus.NEXT, data, msg)
        }

        fun <T> loading(data: T? = null): ResponseState<T> {
            return ResponseState(RequestStatus.LOADING, data, "Loading")
        }
    }
}