package com.sevenlearn.nikestore.feature.checkout

import androidx.lifecycle.MutableLiveData
import com.sevenlearn.nikestore.common.NikeSingleObserver
import com.sevenlearn.nikestore.common.NikeViewModel
import com.sevenlearn.nikestore.common.asyncNetworkRequest
import com.sevenlearn.nikestore.data.Checkout
import com.sevenlearn.nikestore.data.repo.order.OrderRepository

class CheckoutViewModel(orderId: Int, orderRepository: OrderRepository) :
    NikeViewModel() {
    val checkoutLiveData = MutableLiveData<Checkout>()

    init {
        orderRepository.checkout(orderId)
            .asyncNetworkRequest()
            .subscribe(object : NikeSingleObserver<Checkout>(compositeDisposable) {
                override fun onSuccess(t: Checkout) {
                    checkoutLiveData.value = t
                }
            })
    }
}