package com.sevenlearn.nikestore.data

data class CartItem(
    val cart_item_id: Int,
    var count: Int,
    val product: Product
)