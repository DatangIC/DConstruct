package com.datangic.localLock.biometricprompt

import android.os.Build
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.datangic.localLock.R
import com.datangic.localLock.dialog.MaterialDialog

class FingerprintVerifyManager(builder: Builder) {

    private fun isAboveAndroidP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    init {
        val fingerprint = if (isAboveAndroidP()) {
            if (builder.enableAndroidP) {
                FingerprintAndroidP().newInstance().apply {
                    this.fingerprintCallback = builder.callback
                }
            } else {
                FingerprintAndroidM().newInstance().apply {
                    this.fingerprintCallback = builder.callback
                }
            }
        } else {
            FingerprintAndroidM().newInstance()
        }
        if (fingerprint.canAuthenticate(builder.mContext.requireContext())) {
            /**
             * 设定指纹验证框的样式
             */
            // >= Android 6.0
            /**
             * 设定指纹验证框的样式
             */
            // >= Android 6.0
            val bean = VerificationDialogStyleBean()
            bean.cancelTextColor = builder.cancelTextColor
            bean.usepwdTextColor = (builder.usepwdTextColor)
            bean.fingerprintColor = (builder.fingerprintColor)
            bean.usepwdVisible = (builder.usepwdVisible)

            // >= Android 9.0
            bean.title = (builder.title)
            bean.subTitle = (builder.subTitle)
            bean.description = (builder.description)
            bean.cancelBtnText = (builder.cancelBtnText)

            fingerprint.authenticate(builder.mContext, bean, builder.callback)
        } else {
            MaterialDialog.getAlertDialog(builder.mContext.requireContext(), message = R.string.fingerprint_is_not_available, isError = true, isCancel = false).show()
        }
    }

    class Builder(val mContext: Fragment, val callback: FingerprintCallback) {

        /*可选字段*/
        var cancelTextColor = 0
        var usepwdTextColor = 0
        var fingerprintColor = 0
        var usepwdVisible = false

        //在Android 9.0系统上，是否使用系统验证框
        var enableAndroidP = true
        var title: String? = null
        var subTitle: String? = null
        var description: String? = null
        var cancelBtnText //取消按钮文字
                : String? = null

        fun cancelTextColor(@ColorInt color: Int): Builder {
            cancelTextColor = color
            return this
        }

        /**
         * 密码验证按钮文本色
         *
         * @param color
         */
        fun usepwdTextColor(@ColorInt color: Int): Builder {
            usepwdTextColor = color
            return this
        }

        /**
         * 指纹图标颜色
         *
         * @param color
         */
        fun fingerprintColor(@ColorInt color: Int): Builder {
            fingerprintColor = color
            return this
        }

        /**
         * 密码登录按钮是否显示
         *
         * @param isVisible
         */
        fun usepwdVisible(isVisible: Boolean): Builder {
            usepwdVisible = isVisible
            return this
        }

        /**
         * 在 >= Android 9.0 系统上，是否开启google提供的验证方式及验证框
         *
         * @param enableAndroidP
         */
        fun enableAndroidP(enableAndroidP: Boolean): Builder {
            this.enableAndroidP = enableAndroidP
            return this
        }

        /**
         * >= Android 9.0 的验证框的主标题
         *
         * @param title
         */
        fun title(title: String?): Builder {
            this.title = title
            return this
        }

        /**
         * >= Android 9.0 的验证框的副标题
         *
         * @param subTitle
         */
        fun subTitle(subTitle: String?): Builder {
            this.subTitle = subTitle
            return this
        }

        /**
         * >= Android 9.0 的验证框的描述内容
         *
         * @param description
         */
        fun description(description: String?): Builder {
            this.description = description
            return this
        }

        /**
         * >= Android 9.0 的验证框的取消按钮的文字
         *
         * @param cancelBtnText
         */
        fun cancelBtnText(cancelBtnText: String?): Builder {
            this.cancelBtnText = cancelBtnText
            return this
        }

        /**
         * 开始构建
         *
         * @return
         */
        fun build(): FingerprintVerifyManager {
            return FingerprintVerifyManager(this)
        }
    }
}