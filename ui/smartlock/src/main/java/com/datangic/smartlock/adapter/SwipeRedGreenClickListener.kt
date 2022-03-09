package com.datangic.smartlock.adapter

interface SwipeRedGreenClickListener<T> {
    fun onRedClick(any: T)
    fun onGreenClick(any: T)
}