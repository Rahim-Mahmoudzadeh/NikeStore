package com.sevenlearn.nikestore.data.repo

import com.sevenlearn.nikestore.data.Banner
import com.sevenlearn.nikestore.data.repo.source.BannerDataSource
import io.reactivex.Single

class BannerRepositoryImpl(val bannerRemoteDataSource: BannerDataSource) : BannerRepository {
    override fun getBanners(): Single<List<Banner>> = bannerRemoteDataSource.getBanners()
}