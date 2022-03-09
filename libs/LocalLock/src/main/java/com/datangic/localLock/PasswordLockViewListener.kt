package com.datangic.localLock

interface PasswordLockViewListener {
    fun onNumberClick(number: Int, position: Int)
    fun onRemove(position: Int)
    fun onInputDone(password: List<Int>): Boolean
}