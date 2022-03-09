package com.datangic.smartlock.respositorys

import com.datangic.smartlock.R
import com.datangic.smartlock.components.ToolbarProgress

class HomeFragmentRepository {
    val mHomeToolbar by lazy { ToolbarProgress(R.string.title_home) }

    fun getBannerList(): List<Int> {
        return listOf(
                R.mipmap.ad_face_nor2,
                R.mipmap.homepage_adv1,
                R.mipmap.homepage_adv6)
    }

}