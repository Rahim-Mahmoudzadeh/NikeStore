package com.sevenlearn.nikestore.feature.checkout

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_ID
import com.sevenlearn.nikestore.common.formatPrice

import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CheckOutActivity : AppCompatActivity() {
    val viewModel: CheckoutViewModel by viewModel {
        val uri: Uri? = intent.data
        if (uri != null)
            parametersOf(uri.getQueryParameter("order_id")!!.toInt())
        else
            parametersOf(intent.extras!!.getInt(EXTRA_KEY_ID))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        val orderPriceTv=findViewById<TextView>(R.id.orderPriceTv)
        val orderStatusTv=findViewById<TextView>(R.id.orderStatusTv)
        val purchaseStatusTv=findViewById<TextView>(R.id.purchaseStatusTv)
        viewModel.checkoutLiveData.observe(this) {
            orderPriceTv.text = formatPrice(it.payable_price)
            orderStatusTv.text = it.payment_status
            purchaseStatusTv.text =
                if (it.purchase_success) "خرید با موفقیت انجام شد" else "خرید ناموفق"
        }
    }
}