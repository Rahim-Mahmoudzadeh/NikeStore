package com.sevenlearn.nikestore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Banner(
    val id: Int,
    val image: String,
    val link_type: Int,
    val link_value: String
):Parcelable