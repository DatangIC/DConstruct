package com.datangic.localLock.biometricprompt

interface FingerprintCallback {

    /**
     * 无指纹硬件或者指纹硬件不可用
     */
    fun onHwUnavailable()

    /**
     * 未添加指纹
     */
    fun onNoneEnrolled()


    /**
     * 验证成功
     */
    fun onSucceeded()

    /**
     * 验证失败
     */
    fun onFailed()

    /**
     * 密码登录
     */
    fun onUsepwd()

    /**
     * 取消验证
     */
    fun onCancel()

}