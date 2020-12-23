package com.sevenlearn.nikestore.data.repo

import com.sevenlearn.nikestore.data.Banner
import io.reactivex.Single

interface BannerRepository {
    fun getBanners():Single<List<Banner>>
}