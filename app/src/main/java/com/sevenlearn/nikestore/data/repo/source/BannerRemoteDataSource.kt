package com.sevenlearn.nikestore.data.repo.source

import com.sevenlearn.nikestore.data.Banner
import com.sevenlearn.nikestore.services.http.ApiService
import io.reactivex.Single

class BannerRemoteDataSource(val apiService: ApiService) : BannerDataSource {
    override fun getBanners(): Single<List<Banner>> = apiService.getBanners()
}