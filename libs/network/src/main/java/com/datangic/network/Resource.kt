package com.datangic.network

data class Resource<out T>(val requestStatus: RequestStatus, val data: T?, val message: String) {
    companion object {
        fun <T> success(data: T? = null): Resource<T> {
            return Resource(RequestStatus.SUCCESS, data, "Success")
        }

        fun <T> error(msg: String, data: T? = null): Resource<T> {
            return Resource(RequestStatus.ERROR, data, msg)
        }

        fun <T> empty(msg: String, data: T? = null): Resource<T> {
            return Resource(RequestStatus.EMPTY, data, msg)
        }

        fun <T> next(msg: String, data: T? = null): Resource<T> {
            return Resource(RequestStatus.NEXT, data, msg)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(RequestStatus.LOADING, data, "Loading")
        }
    }
}