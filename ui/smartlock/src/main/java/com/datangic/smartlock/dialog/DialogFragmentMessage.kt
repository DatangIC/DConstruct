package com.datangic.smartlock.dialog

import android.app.Dialog
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

open class DialogFragmentMessage : DialogFragment() {
    private val TAG = DialogFragmentMessage::class.simpleName

    companion object {
        val ICON_RES_ID = "ICON_RES_ID"
        val TITLE = "TITLE"
        val MESSAGE = "MESSAGE"
        val ERROR = "ERROR"
    }

    protected var dialogBuilder: AlertDialog.Builder? = null
    protected var title: Any? = null
    protected var message: Any? = null
    protected var isError: Boolean? = false
    private var newMessage: Any? = null
    private var newTitle: Any? = null
    protected var icon: Int? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = arguments?.getSerializable(TITLE)
            message = arguments?.getSerializable(MESSAGE)
            icon = arguments?.getInt(ICON_RES_ID)
            isError = arguments?.getBoolean(ERROR)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogBuilder?.let {
            if (newTitle != null) {
                when (newTitle) {
                    is Int -> it.setTitle(newTitle as Int)
                    is String -> it.setTitle(newTitle as String)
                    else -> return@let
                }
            } else {
                when (title) {
                    is Int -> it.setTitle(title as Int)
                    is String -> it.setTitle(title as String)
                    else -> return@let
                }
            }
        }
        if (newMessage != null) {
            if (newMessage is Int) {
                dialogBuilder!!.setMessage(newMessage as Int)
            } else if (newMessage is String) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    dialogBuilder!!.setMessage(Html.fromHtml(newMessage.toString(), Html.FROM_HTML_MODE_COMPACT))
                else {
                    dialogBuilder!!.setMessage(Html.fromHtml(newMessage.toString()))
                }
            }
        } else {
            when (message) {
                is Int -> dialogBuilder!!.setMessage(message as Int)
                is String -> dialogBuilder!!.setMessage(message as String)
            }
        }
        alertDialog = dialogBuilder!!.show()
        if ((alertDialog?.window?.findViewById<View>(android.R.id.icon) as ImageView).drawable is AnimatedVectorDrawable) {
            ((alertDialog?.window?.findViewById<View>(android.R.id.icon) as ImageView).drawable as AnimatedVectorDrawable).start()
        }
        return alertDialog!!
    }


    fun setTitleTip(value: Any) {
        newTitle = value
        if (value is String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                alertDialog?.setTitle(Html.fromHtml(value.toString(), Html.FROM_HTML_MODE_COMPACT))
            else {
                alertDialog?.setTitle(Html.fromHtml(value.toString()))
            }
        }
    }

    fun setMessageTip(value: Any) {
        newMessage = value
        if (value is String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                alertDialog?.setMessage(Html.fromHtml(value.toString(), Html.FROM_HTML_MODE_COMPACT))
            else {
                alertDialog?.setMessage(Html.fromHtml(value.toString()))
            }
        }
    }

    fun setIcon(icon: Int) {
        alertDialog?.setIcon(icon)
    }

    fun isError(boolean: Boolean) {
        isError = boolean
    }

    fun setPositiveButtonClickable(clickable: Boolean) {
        (alertDialog?.window?.findViewById<View>(android.R.id.button1) as Button).isClickable = clickable
    }
}