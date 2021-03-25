package com.sevenlearn.nikestore.feature.main

import com.sevenlearn.nikestore.common.NikeSingleObserver
import com.sevenlearn.nikestore.common.NikeViewModel
import com.sevenlearn.nikestore.data.CartItemCount
import com.sevenlearn.nikestore.data.TokenContainer
import com.sevenlearn.nikestore.data.repo.CartRepository
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class MainViewModel(private val cartRepository: CartRepository) : NikeViewModel() {
    fun getCartItemsCount(){
        if (!TokenContainer.token.isNullOrEmpty()){
            cartRepository.getCartItemsCount()
                .subscribeOn(Schedulers.io())
                .subscribe(object :NikeSingleObserver<CartItemCount>(compositeDisposable){
                    override fun onSuccess(t: CartItemCount) {
                        EventBus.getDefault().postSticky(t)
                    }
                })
        }
    }
}