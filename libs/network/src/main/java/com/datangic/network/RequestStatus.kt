package com.datangic.network

enum class RequestStatus(val value: Int) {
    SUCCESS(0),
    ERROR(-1),
    LOADING(1),
    EMPTY(2),
    NEXT(3)
}