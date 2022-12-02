package com.datangic.api

data class Page<T>(
    val total: Int,
    val pageSize: Int,
    val pageNum: Int,
    val nextPage: Int,
    val pages: Int,
    val prePage: Int,
    val size: Int,
    val startRow: Int,
    val endRow: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val list: ArrayList<T>,
    val navigateFirstPage: Int,
    val navigateLastPage: Int,
    val navigatePages: Int,
    val navigatepageNums: ArrayList<Int>
)
