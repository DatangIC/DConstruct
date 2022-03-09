package com.datangic.smartlock.adapter

import android.os.Build
import android.text.Html
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar

object ViewBindingAdapter {


    @BindingAdapter("android:src")
    @JvmStatic
    fun setSrc(view: ImageView, resId: Int) {
        if (resId != 0)
            view.setImageResource(resId)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, text: Any) {
        if (text is Int && text != 0) {
            view.setText(text)
        } else if (text is String) {
            view.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
    }

    @BindingAdapter("toolBarTitle")
    @JvmStatic
    fun setToolBarTitle(view: MaterialToolbar, title: Any?) {
        if (title is String)
            view.title = title
        else if (title is Int && title != 0) {
            view.setTitle(title)
        }
    }

    @BindingAdapter("toolBarSubTitle")
    @JvmStatic
    fun setToolBarSubTitle(view: MaterialToolbar, subTitle: Int) {
        if (subTitle != 0)
            view.setSubtitle(subTitle)
    }

    @BindingAdapter("animated_checked")
    @JvmStatic
    fun setAnimatedChecked(view: ImageButton, checked: Boolean) {
        view.setImageState(if (checked) intArrayOf(android.R.attr.state_selected) else intArrayOf(), true)
    }

}