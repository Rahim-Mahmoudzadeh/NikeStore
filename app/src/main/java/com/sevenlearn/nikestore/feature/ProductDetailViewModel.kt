package com.sevenlearn.nikestore.feature

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.sevenlearn.nikestore.common.EXTRA_KEY_DATA
import com.sevenlearn.nikestore.common.NikeViewModel
import com.sevenlearn.nikestore.data.Product

class ProductDetailViewModel(bundle: Bundle) : NikeViewModel() {
    val productLiveData = MutableLiveData<Product>()

    init {
        productLiveData.value = bundle.getParcelable(EXTRA_KEY_DATA)
    }
}