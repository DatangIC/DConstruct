package com.datangic.components.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.datangic.themes.R

data class TipsDialogState(
    val title: MutableState<Int> = mutableStateOf(R.string.notice),
    val text: MutableState<Int> = mutableStateOf(R.string.notice_explain),
    private val show: MutableState<Boolean> = mutableStateOf(false),
) {
    fun changeTitle(i: Int): TipsDialogState {
        title.value = i
        return this
    }

    fun changeText(i: Int): TipsDialogState {
        text.value = i
        return this
    }

    fun close(): TipsDialogState {
        show.value = false
        return this
    }

    fun show(): TipsDialogState {
        show.value = true
        return this
    }

    fun isShow(): Boolean {
        return show.value
    }

    fun change(title: Int, text: Int): TipsDialogState {
        this.title.value = title
        this.text.value = text
        return this
    }
}