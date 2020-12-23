package com.sevenlearn.nikestore

import android.app.Application
import android.os.Bundle
import com.facebook.drawee.backends.pipeline.Fresco
import com.sevenlearn.nikestore.data.repo.BannerRepository
import com.sevenlearn.nikestore.data.repo.BannerRepositoryImpl
import com.sevenlearn.nikestore.data.repo.ProductRepository
import com.sevenlearn.nikestore.data.repo.ProductRepositoryImpl
import com.sevenlearn.nikestore.data.repo.source.BannerRemoteDataSource
import com.sevenlearn.nikestore.data.repo.source.ProductLocalDataSource
import com.sevenlearn.nikestore.data.repo.source.ProductRemoteDataSource
import com.sevenlearn.nikestore.feature.ProductDetailViewModel
import com.sevenlearn.nikestore.feature.main.MainViewModel
import com.sevenlearn.nikestore.feature.main.ProductListAdapter
import com.sevenlearn.nikestore.services.FrescoImageLoadingService
import com.sevenlearn.nikestore.services.ImageLoadingService
import com.sevenlearn.nikestore.services.http.ApiService
import com.sevenlearn.nikestore.services.http.createApiServiceInstance
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        Fresco.initialize(this)

        val myModules = module {
            single { createApiServiceInstance() }
            single<ImageLoadingService> { FrescoImageLoadingService() }
            factory<ProductRepository> {
                ProductRepositoryImpl(
                    ProductRemoteDataSource(get()),
                    ProductLocalDataSource()
                )
            }
            factory { ProductListAdapter(get()) }
            factory<BannerRepository> { BannerRepositoryImpl(BannerRemoteDataSource(get())) }
            viewModel { MainViewModel(get(), get()) }
            viewModel { (bundle: Bundle) -> ProductDetailViewModel(bundle) }
        }

        startKoin {
            androidContext(this@App)
            modules(myModules)
        }
    }
}