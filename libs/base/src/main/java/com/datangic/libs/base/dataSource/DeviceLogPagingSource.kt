package com.datangic.libs.base.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.datangic.data.database.table.DeviceLog

class DeviceLogPagingSource() : PagingSource<Int, DeviceLog>() {
    override fun getRefreshKey(state: PagingState<Int, DeviceLog>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DeviceLog> {
        try {

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}