package com.datangic.smartlock.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.*
import android.text.Html
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import com.datangic.smartlock.R
import com.datangic.common.file.LockFile
import com.datangic.smartlock.utils.UtilsFormat.toHtml
import com.datangic.smartlock.utils.UtilsFormat.toStringWithTime
import com.datangic.smartlock.utils.UtilsFormat.toTimeStamp
import com.datangic.smartlock.utils.UtilsFormat.toTimeString
import com.datangic.smartlock.utils.UtilsMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
import com.google.android.material.textview.MaterialTextView

object MaterialDialog {

    interface OnMaterialAlterDialogListener {
        fun onCancel()
        fun onConfirm()
    }

    interface OnMaterialConfirmationForSecretCodeDialogListener {
        fun onSelected(selected: String)
        fun onAdd()
        fun onDelete(selected: String)
        fun onConfirm()
    }

    interface OnMaterialConfirmationDialogListener {
        fun onCancel()
        fun onSelected(selected: String)
        fun onConfirm(selected: String)
    }


    private val TAG = MaterialDialog::class.simpleName

    private fun MaterialAlertDialogBuilder.setItems(items: Array<Int>, onClickListener: DialogInterface.OnClickListener) {
        val mItems = arrayOfNulls<String>(items.size)
        for (i in items.indices) {
            mItems[i] = this.context.getString(items[i])
        }
        this.setItems(mItems, onClickListener)
    }

    fun AlertDialog.setIcon(icon: Any?) {
        when (icon) {
            is Int ->
                this.setIcon(icon)
            else -> return
        }
    }

    fun AlertDialog.setTitle(title: Any?) {
        when (title) {
            is Int ->
                this.setTitle(title)
            is String ->
                this.setTitle(title)
            else -> return
        }
    }


    fun AlertDialog.setImageLevel(@IntRange(from = 0, to = 4) level: Int) {
        this.findViewById<ImageView>(R.id.level_image)?.setImageLevel(level)
    }

    fun AlertDialog.show(timeout: Long) {
        this.show()
        Handler(Looper.myLooper()!!).postDelayed({
            this.cancel()
        }, timeout)
    }

    fun AlertDialog.setMessage(message: Any?) {
        this.findViewById<TextView>(R.id.dialog_tips)?.let {
            when (message) {
                is Int -> it.setText(message)
                is String -> it.text = message
                else -> {
                }
            }
        } ?: let {
            when (message) {
                is Int ->
                    this.setMessage(this.context.getText(message))
                is String ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        this.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
                    else {
                        this.setMessage(Html.fromHtml(message))
                    }
                else -> return
            }
        }
    }

    private fun MaterialAlertDialogBuilder.setIcon(icon: Any?) {
        when (icon) {
            is Int ->
                this.setIcon(icon)
            else -> return
        }
    }

    fun MaterialAlertDialogBuilder.setTitle(title: Any?) {
        when (title) {
            is Int ->
                if (title != 0)
                    this.setTitle(title)
            is String ->
                this.setTitle(title)
            else -> return
        }
    }


    fun MaterialAlertDialogBuilder.setMessage(message: Any?) {
        when (message) {
            is Int ->
                if (message != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        this.setMessage(Html.fromHtml(this.context.getText(message).toString(), Html.FROM_HTML_MODE_COMPACT))
                    else this.setMessage(Html.fromHtml(this.context.getText(message).toString()))
                }
            is String ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    this.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
                else {
                    this.setMessage(Html.fromHtml(message))
                }
            else -> return
        }
    }

    fun getBottomSheetDialogWithLayout(
        context: Context,
        @LayoutRes layoutResId: Int = R.layout.bottom_sheet_menu,
        title: Any? = null,
        cancelAction: (() -> Unit)? = null
    ): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(layoutResId)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.behavior.apply {
            isFitToContents = true
            isDraggable = true
            isHideable = true
        }
        when (title) {
            is Int -> bottomSheetDialog.findViewById<MaterialTextView>(R.id.dialog_title)?.setText(title)
            is String ->
                bottomSheetDialog.findViewById<MaterialTextView>(R.id.dialog_title)?.text = title
            else -> {
            }
        }
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setOnCancelListener { cancelAction?.let { it() } }
        return bottomSheetDialog
    }


    fun getAlertDialog(
        context: Context,
        icon: Int? = R.drawable.ic_tips,
        title: Any? = R.string.notice,
        message: Any,
        isError: Boolean = false,
        isCancel: Boolean = true,
        isConfirm: Boolean = true,
        action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {
        val mDialogBuilder =
            MaterialAlertDialogBuilder(context, if (isError) R.style.AppTheme_DialogError else R.style.AppTheme_MaterialDialog).apply {
                setTitle(title)
                setMessage(message)
                setIcon(icon)
                setCancelable(true)
            }
        if (isCancel)
            mDialogBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                run {
                    action?.onCancel()
                    dialog.cancel()
                }
            }
        if (isConfirm)
            mDialogBuilder.setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                run {
                    action?.onConfirm()
                    dialog.cancel()
                }
            }
        return mDialogBuilder.create()
    }


    fun getLoadingDialog(
        context: Context,
        message: Any = R.string.waite,
        isCancel: Boolean = true,
        action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {
        return getAlertDialog(
            context,
            R.drawable.animated_loading,
            R.string.waite,
            message,
            isError = false,
            isCancel,
            isConfirm = false,
            action
        ).apply {
            setCancelable(false)
            setOnDismissListener {
                action?.onCancel()
            }
            setOnShowListener {
                if ((this.window?.findViewById<View>(android.R.id.icon) as ImageView).drawable is AnimatedVectorDrawable) {
                    ((this.window?.findViewById<View>(android.R.id.icon) as ImageView).drawable as AnimatedVectorDrawable).start()
                }
            }
        }
    }

    fun getDelayDialog(
        context: Context,
        message: Any,
        title: Any = R.string.setting_restore_factory_settings,
        delay: Int = 5,
        isRed: Boolean = true,
        action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {

        return getAlertDialog(
            context,
            icon = R.drawable.ic_warning,
            title,
            message,
            isError = isRed,
            isCancel = true,
            isConfirm = false,
            action
        ).also {
            it.setCancelable(false)
            timer(it, delay) {
                action?.onConfirm()
            }.start()
        }
    }

    private fun timer(dialog: AlertDialog, delay: Int, action: (() -> Unit)? = null) = object : CountDownTimer(delay * 1000L, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            dialog.getButton(BUTTON_POSITIVE).apply {
                setTextAppearance(R.style.AppTheme_TextAppearance_Disable)
                visibility = View.VISIBLE
                text = dialog.context.getString(R.string.delay_second).format(millisUntilFinished / 1000 + 1)
                isEnabled = false
            }
        }

        override fun onFinish() {
            dialog.getButton(BUTTON_POSITIVE).apply {
                setTextAppearance(R.style.AppTheme_TextAppearance_Warning)
                text = dialog.context.getString(R.string.confirm)
                visibility = View.VISIBLE
                isEnabled = true
                setOnClickListener {
                    kotlin.run {
                        action?.let {
                            it()
                        }
                        dialog.cancel()
                    }
                }
            }
        }
    }

    fun getTimePeriodPickerDialog(
        context: Context,
        startTime: Int,
        endTime: Int,
        isEndLager: Boolean = false,
        action: ((Int, Int) -> Unit)? = null
    ): AlertDialog {
        val format = "yyyy-MM-dd HH:mm"
        val startTime1 = (startTime.toTimeString()).split(":")
        val endTime1 = (endTime.toTimeString()).split(":")
        val date = ((System.currentTimeMillis() / 1000).toInt()).toTimeString(format)
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_timer_picker, null).apply {
            findViewById<TimePicker>(R.id.time_start)?.apply {
                setIs24HourView(true)
                hour = startTime1[0].toInt()
                minute = startTime1[1].toInt()
            }
            findViewById<TimePicker>(R.id.time_end)?.apply {
                setIs24HourView(true)
                hour = endTime1[0].toInt()
                minute = endTime1[1].toInt()
            }
        }
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setTitle(R.string.time_picker)
            setIcon(R.drawable.ic_time)
            setView(layout)
            setPositiveButton(R.string.confirm) { _, _ ->
                action?.let {
                    var statTimeDone = date
                    var endTimeDone = date
                    layout.findViewById<TimePicker>(R.id.time_start)?.let {
                        statTimeDone = statTimeDone.replaceRange(11, 13, it.hour.toStringWithTime())
                        statTimeDone = statTimeDone.replaceRange(14, 16, it.minute.toStringWithTime())
                    }
                    layout.findViewById<TimePicker>(R.id.time_end)?.let {
                        endTimeDone = endTimeDone.replaceRange(11, 13, it.hour.toStringWithTime())
                        endTimeDone = endTimeDone.replaceRange(14, 16, it.minute.toStringWithTime())
                    }
                    val startTimestamp = (statTimeDone.toTimeStamp(format) / 1000).toInt()
                    var endTimestamp = (endTimeDone.toTimeStamp(format) / 1000).toInt()
                    if (endTimestamp <= startTimestamp) {
                        if (!isEndLager) {
                            endTimestamp += 86400
                        }
                    }
                    it(startTimestamp, endTimestamp)
                }
            }
            setNegativeButton(R.string.cancel, null)
            setNeutralButton(R.string.close) { _, _ ->
                action?.let { it(0, 0) }
            }
        }.create()
    }

    fun getPasswordDialog(context: Context, length: Int = 6, action: ((String) -> Unit)?): AlertDialog {
        var passwordView: TextInputLayout?
        var confirmPasswordView: TextInputLayout?
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_input_key_value, null).also {
            passwordView = it.findViewById<TextInputLayout>(R.id.key).apply {
                helperText = context.getString(R.string.password_length_tip).format(length)
                endIconMode = END_ICON_PASSWORD_TOGGLE
                isCounterEnabled = true
                counterMaxLength = length
                hint = context.getString(R.string.password)
                editText?.inputType = EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD or EditorInfo.TYPE_CLASS_NUMBER
                editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
            }
            confirmPasswordView = it.findViewById<TextInputLayout>(R.id.value).apply {
                endIconMode = END_ICON_PASSWORD_TOGGLE
                isCounterEnabled = true
                counterMaxLength = length
                hint = context.getString(R.string.confirm_password)
                editText?.maxLines = 1
                editText?.inputType = EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD or EditorInfo.TYPE_CLASS_NUMBER
                editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
            }
        }
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setTitle(R.string.dialog_title_password)
            setIcon(R.drawable.ic_password2_36)
            setView(layout)
            setPositiveButton(R.string.confirm, null)
            setNegativeButton(R.string.cancel, null)
        }.create().apply {
            setOnShowListener {
                this.getButton(BUTTON_POSITIVE).setOnClickListener {
                    if (passwordView == null || confirmPasswordView == null)
                        return@setOnClickListener
                    else {
                        if (passwordView?.editText?.text.toString() == confirmPasswordView!!.editText?.text.toString() && passwordView!!.editText?.text?.length == length) {
                            action?.let { it(passwordView!!.editText?.text.toString()) }
                            this.dismiss()
                        } else {
                            UtilsMessage.displaySnackBar(
                                it,
                                if (passwordView?.editText?.text?.length != length) context.getString(R.string.password_length_error)
                                    .format(length) else R.string.password_confirm_incorrect
                            )
                            return@setOnClickListener
                        }
                    }
                }
            }
        }
    }

    fun getSecretCodeDialog(
        context: Context,
        length: Int = 10,
        cancelAction: (() -> Unit)?,
        confirmAction: ((String, String) -> Unit)?
    ): AlertDialog {
        var keyView: TextInputLayout?
        var valueView: TextInputLayout?
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_input_key_value, null).also {
            keyView = it.findViewById<TextInputLayout>(R.id.key).apply {
                helperText = context.getString(R.string.secret_code_key_tip)
                isCounterEnabled = true
                hint = context.getString(R.string.secret_code_name_tip)
            }
            valueView = it.findViewById<TextInputLayout>(R.id.value).apply {
                helperText = context.getString(R.string.secret_code_value_tip).format(length)
                isCounterEnabled = true
                counterMaxLength = length
                editText?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(length))
                hint = context.getString(R.string.secret_code_value_tip)
            }
        }
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setTitle(R.string.dialog_title_secret_code)
            setIcon(R.drawable.ic_secret)
            setView(layout)
            setPositiveButton(R.string.confirm, null)
            setNegativeButton(R.string.cancel, null)
        }.create().apply {
            setOnDismissListener {
                cancelAction?.let { cancelAction() }
            }
            setOnShowListener {
                this.getButton(BUTTON_POSITIVE).setOnClickListener {
                    if (keyView == null || valueView == null)
                        return@setOnClickListener
                    else {
                        if (valueView!!.editText?.text?.length == length) {
                            confirmAction?.let { confirmAction(keyView!!.editText?.text.toString(), valueView!!.editText?.text.toString()) }
                            this.dismiss()
                        } else {
                            UtilsMessage.displaySnackBar(it, context.getString(R.string.secret_code_length_error).format(length))
                            return@setOnClickListener
                        }
                    }
                }
            }
        }
    }

    fun getInputStringDialog(
        context: Context,
        title: Any?,
        icon: Int?,
        hint: Any = 0,
        maxLength: Int = 10,
        tips: Int = 0,
        errorTips: Int = R.string.dialog_name_input_error,
        action: ((String) -> Unit)?
    ): AlertDialog {
        var inputView: TextInputLayout?
        val hitString = when (hint) {
            is Int -> {
                if (hint == 0) "" else context.getString(hint)
            }
            is String -> {
                hint
            }
            else -> {
                ""
            }
        }
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_input_string, null).also {
            inputView = it.findViewById<TextInputLayout>(R.id.input_string).apply {
                if (tips != 0) {
                    helperText = context.getString(tips)
                }
                isCounterEnabled = true
                this.hint = hitString
                counterMaxLength = maxLength
            }
        }
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setTitle(title)
            setIcon(icon)
            setView(layout)
            setPositiveButton(R.string.confirm, null)
            setNegativeButton(R.string.cancel, null)
        }.create().apply {
            setOnShowListener {
                this.getButton(BUTTON_POSITIVE).setOnClickListener { view ->
                    if (inputView == null)
                        return@setOnClickListener
                    else {
                        inputView?.editText?.text?.toString()?.let { str ->
                            if (str.isEmpty()) {
                                cancel()
                            } else {
                                if (maxLength != 0 && str.length > maxLength) {
                                    UtilsMessage.displaySnackBar(view, errorTips)
                                } else {
                                    action?.let { it(str) }
                                    cancel()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    fun getWifiAddInfoDialog(
        context: Context,
        icon: Int?,
        title: Any?,
        wifi: String,
        isDebug: Boolean = false,
        action: ((String, String, String) -> Unit)? = null
    ): AlertDialog {
        var serverIp: TextInputLayout?
        var serverPort: TextInputLayout?
        var wifiPwd: TextInputLayout?
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_input_wifi_info, null).also { view ->
            view.findViewById<TextView>(R.id.wifi_ip).text = context.getString(R.string.dialog_wifi_wifi_ip).format(wifi).toHtml()
            serverIp = view.findViewById(R.id.server_ip)
            serverPort = view.findViewById(R.id.server_port)
            wifiPwd = view.findViewById<TextInputLayout>(R.id.wifi_pwd).apply {
                this.endIconMode = END_ICON_PASSWORD_TOGGLE
            }

            if (isDebug) {
                serverIp?.visibility = View.VISIBLE
                serverPort?.visibility = View.VISIBLE
            } else {
                serverIp?.visibility = View.GONE
                serverPort?.visibility = View.GONE
            }
        }
        return MaterialAlertDialogBuilder(
            context,
            R.style.AppTheme_MaterialDialog
        ).apply {
            setTitle(title)
            setIcon(icon)
            setView(layout)
            setPositiveButton(R.string.confirm, null)
            setNegativeButton(R.string.cancel, null)
        }.create().apply {
            setOnShowListener {
                this.getButton(BUTTON_POSITIVE).setOnClickListener {
                    if (isDebug) {
                        var ip = ""
                        var port = ""
                        serverIp?.editText?.text.toString().let { _ip ->
                            val regex = Regex("^\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}$")
                            if (regex.matches(_ip)) {
                                serverIp?.isErrorEnabled = false
                                ip = _ip
                            } else {
                                serverIp?.isErrorEnabled = true
                                serverIp?.error = context.getString(R.string.dialog_wifi_server_ip_error)
                                return@setOnClickListener
                            }
                        }
                        serverPort?.editText?.text.toString().let { _port ->
                            val regex = Regex("^\\d{1,6}$")
                            if (regex.matches(_port)) {
                                serverPort?.isErrorEnabled = false
                                port = _port
                            } else {
                                serverPort?.isErrorEnabled = true
                                serverPort?.error = context.getString(R.string.dialog_wifi_server_port_error)
                                return@setOnClickListener
                            }

                        }
                        wifiPwd?.editText?.text.toString().let { pwd ->
                            action?.let { a ->
                                a(ip, port, pwd)
                                this.cancel()
                            }
                        }

                    } else {
                        wifiPwd?.editText?.text.toString().let { pwd ->
                            action?.let { a ->
                                a("", "", pwd)
                                this.cancel()
                            }
                        }
                    }
                }
            }
        }
    }

    fun getAddFingerprintDialog(context: Context, action: OnMaterialAlterDialogListener? = null): AlertDialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_fingerprint, null)
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setTitle(R.string.management_fingerprint)
            setView(layout)
            setNegativeButton(R.string.cancel) { dialog, _ ->
                action?.onCancel()
                dialog.cancel()
            }
        }.create()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun getConfirmationForSecretCodeDialog(
        context: Context,
        icon: Int? = R.drawable.ic_tips,
        title: Any? = R.string.notice,
        message: Array<String>,
        selected: Any,
        isError: Boolean = false,
        hasNeutral: Boolean = true,
        action: OnMaterialConfirmationForSecretCodeDialogListener? = null
    ): AlertDialog {
        var whichId: Int = when (selected) {
            is Int -> selected
            is String -> message.indexOf(selected)
            else -> 0
        }
        return MaterialAlertDialogBuilder(context, if (isError) R.style.AppTheme_DialogError else R.style.AppTheme_MaterialDialog).apply {
            setIcon(icon)
            setTitle(title)
            setPositiveButton(R.string.confirm) { _, _ ->
                action?.onConfirm()
            }
            if (hasNeutral) {
                setNeutralButton(R.string.delete) { _, _ ->
                    action?.onDelete(message[whichId])
                }
                setNegativeButton(R.string.add) { _, _ ->
                    action?.onAdd()
                }
            }
            setSingleChoiceItems(
                message, whichId
            ) { _, which ->
                whichId = which
                action?.onSelected(message[which])
            }
        }.create()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getConfirmationDialog(
        context: Context,
        icon: Int? = R.drawable.ic_tips,
        title: Any? = R.string.notice,
        message: Array<String>,
        selected: Any,
        isError: Boolean = false,
        action: OnMaterialConfirmationDialogListener? = null
    ): AlertDialog {
        var whichId: Int = when (selected) {
            is Int -> selected
            is String -> message.indexOf(selected)
            else -> 0
        }
        return MaterialAlertDialogBuilder(context, if (isError) R.style.AppTheme_DialogError else R.style.AppTheme_MaterialDialog).apply {
            setIcon(icon)
            setTitle(title)
            setPositiveButton(R.string.confirm) { _, _ ->
                action?.onConfirm(message[whichId])
            }
            setNegativeButton(R.string.cancel) { _, _ ->
                action?.onCancel()
            }
            setSingleChoiceItems(
                message, whichId
            ) { _, which ->
                whichId = which
                action?.onSelected(message[which])
            }
        }.create()
    }

    fun getTempPwdCreateDialog(context: Context, action: ((Boolean, Int) -> Unit)? = null): AlertDialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_temp_pwd, null).apply {
            with(findViewById<NumberPicker>(R.id.number_picker)) {
                minValue = 1
                maxValue = 24
            }
            findViewById<RadioButton>(R.id.once_true).isChecked = true
        }
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setTitle(R.string.dialog_temp_pwd_title)
            setIcon(R.drawable.ic_temp_pwd_36)
            setView(layout)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                action?.let {
                    it(layout.findViewById<RadioButton>(R.id.once_true).isChecked, layout.findViewById<NumberPicker>(R.id.number_picker).value)
                }
                dialog.cancel()
            }
            setNegativeButton(R.string.cancel, null)
        }.create()
    }

    fun getItemDialog(
        context: Context,
        items: Array<Int>,
        action: ((Int) -> Unit)?
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setItems(items) { _, which ->
                action?.let { it(which) }
            }
        }.create()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun getShareDialog(
        context: Context,
        icon: Int? = R.drawable.ic_tips,
        title: Any? = R.string.dialog_share,
        message: Any,
        isError: Boolean = false,
        isCancel: Boolean = true,
        isShare: Boolean = true,
        action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {
        val mDialogBuilder =
            MaterialAlertDialogBuilder(context, if (isError) R.style.AppTheme_DialogError else R.style.AppTheme_MaterialDialog).apply {
                setTitle(title)
                setMessage(message)
                setIcon(icon)
                setCancelable(true)
            }
        if (isCancel)
            mDialogBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                run {
                    action?.onCancel()
                    dialog.cancel()
                }
            }
        if (isShare) {
            mDialogBuilder.setPositiveButton(context.getString(R.string.dialog_share)) { dialog, _ ->
                run {
                    if (message is String) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_TEXT, message)
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.dialog_share)))
//                        "Choose a channel to share your text..."
                    }
                    action?.onConfirm()
                    dialog.cancel()
                }
            }
        }
        return mDialogBuilder.create()
    }

    fun getBitmapDialog(
        context: Context,
        title: Any? = R.string.dialog_share,
        icon: Any = R.drawable.ic_share_24,
        bitmap: Bitmap,
        action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_bitmap, null)
        val mDialogBuilder = MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setView(layout)
            setIcon(icon)
            setTitle(title ?: R.string.dialog_share)
            layout.findViewById<ImageView>(R.id.dialog_image).setImageBitmap(bitmap)
            setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                action?.onCancel()
            }
            setPositiveButton(context.getString(R.string.dialog_share)) { _, _ ->
                run {
                    LockFile.saveBitMap(context, bitmap, if (title is String) title else context.getString(R.string.dialog_share))?.let { uri ->
                        UtilsMessage.share(context, uri)
                    }
                }
                action?.onConfirm()
            }
        }
        return mDialogBuilder.create()
    }


    fun getDatePickerDialog(
        dataRange: Boolean = false,
        title: Any?,
        validationChoice: CalendarConstraints.DateValidator = DateValidatorPointForward.now(),
        openingAt: Long = MaterialDatePicker.todayInUtcMilliseconds(),
        boundsStart: Long = 0,
        boundsEnd: Long = 0,
        inputModel: Int = MaterialDatePicker.INPUT_MODE_CALENDAR,
        @SuppressLint("ResourceType") @StyleRes theme: Int = R.style.ThemeOverlay_Toolbar,
    ): MaterialDatePicker<*> {
        val constraints = CalendarConstraints.Builder().apply {
            setValidator(validationChoice)
            setOpenAt(openingAt)
            if (boundsStart != 0L) {
                setStart(boundsStart)
                if (boundsEnd != 0L)
                    setEnd(boundsEnd)
            }
        }
        val builder = if (dataRange) {
            MaterialDatePicker.Builder.dateRangePicker()
        } else {
            MaterialDatePicker.Builder.datePicker()
        }.apply {
            setInputMode(inputModel)
            setTheme(theme)
            when (title) {
                is Int -> setTitleText(title)
                is String -> setTitleText(title)
            }
            setCalendarConstraints(constraints.build())
        }
        return builder.build()
    }
}