package com.sevenlearn.nikestore

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import com.facebook.drawee.backends.pipeline.Fresco
import com.sevenlearn.nikestore.data.repo.*
import com.sevenlearn.nikestore.data.repo.order.OrderRemoteDataSource
import com.sevenlearn.nikestore.data.repo.order.OrderRepository
import com.sevenlearn.nikestore.data.repo.order.OrderRepositoryImpl
import com.sevenlearn.nikestore.data.repo.source.*
import com.sevenlearn.nikestore.feature.ProductDetailViewModel
import com.sevenlearn.nikestore.feature.auth.AuthViewModel
import com.sevenlearn.nikestore.feature.cart.CartViewModel
import com.sevenlearn.nikestore.feature.checkout.CheckoutViewModel
import com.sevenlearn.nikestore.feature.common.ProductListAdapter
import com.sevenlearn.nikestore.feature.list.ProductListViewModel
import com.sevenlearn.nikestore.feature.home.HomeViewModel
import com.sevenlearn.nikestore.feature.main.MainViewModel
import com.sevenlearn.nikestore.feature.product.comment.CommentListViewModel
import com.sevenlearn.nikestore.feature.profile.ProfileViewModel
import com.sevenlearn.nikestore.feature.shipping.ShippingViewModel
import com.sevenlearn.nikestore.services.FrescoImageLoadingService
import com.sevenlearn.nikestore.services.ImageLoadingService
import com.sevenlearn.nikestore.services.http.createApiServiceInstance
import org.koin.android.ext.android.get
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

            single<SharedPreferences> {
                this@App.getSharedPreferences(
                    "app_settings",
                    MODE_PRIVATE
                )
            }
            single { UserLocalDataSource(get()) }
            single<UserRepository> {
                UserRepositoryImpl(
                    UserLocalDataSource(get()),
                    UserRemoteDataSource(get())
                )
            }
            single<OrderRepository> { OrderRepositoryImpl(OrderRemoteDataSource(get())) }

            factory { (viewType: Int) -> ProductListAdapter(viewType, get()) }
            factory<BannerRepository> { BannerRepositoryImpl(BannerRemoteDataSource(get())) }
            factory<CommentRepository> { CommentRepositoryImpl(CommentRemoteDataSource(get())) }
            factory<CartRepository> { CartRepositoryImpl(CartRemoteDataSource(get())) }
            viewModel { HomeViewModel(get(), get()) }
            viewModel { (bundle: Bundle) -> ProductDetailViewModel(bundle, get(), get()) }
            viewModel { (productId: Int) -> CommentListViewModel(productId, get()) }
            viewModel { (sort: Int) -> ProductListViewModel(sort, get()) }
            viewModel { AuthViewModel(get()) }
            viewModel { CartViewModel(get()) }
            viewModel { MainViewModel(get()) }
            viewModel { ShippingViewModel(get()) }
            viewModel { (orderId: Int) -> CheckoutViewModel(orderId, get()) }
            viewModel { ProfileViewModel(get()) }
        }

        startKoin {
            androidContext(this@App)
            modules(myModules)
        }

        val userRepository: UserRepository = get()
        userRepository.loadToken()
    }
}