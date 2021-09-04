package com.sevenlearn.nikestore.feature.shipping

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_DATA
import com.sevenlearn.nikestore.common.EXTRA_KEY_ID
import com.sevenlearn.nikestore.common.NikeSingleObserver
import com.sevenlearn.nikestore.common.openUrlInCustomTab
import com.sevenlearn.nikestore.data.PurchaseDetail
import com.sevenlearn.nikestore.data.SubmitOrderResult
import com.sevenlearn.nikestore.feature.cart.CartItemAdapter
import com.sevenlearn.nikestore.feature.checkout.CheckOutActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalStateException
import org.koin.android.viewmodel.ext.android.viewModel

class ShippingActivity : AppCompatActivity() {
    val viewModel: ShippingViewModel by viewModel()
    val compositeDisposable = CompositeDisposable()
    var firstNameEt :EditText?=null
    var lastNameEt :EditText?=null
    var phoneNumberEt :EditText?=null
    var postalCodeEt :EditText?=null
    var addressEt :EditText?=null
    var onlinePaymentBtn : MaterialButton?=null
    var codBtn :MaterialButton?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping)
        firstNameEt=findViewById(R.id.firstNameEt)
        lastNameEt=findViewById(R.id.lastNameEt)
        phoneNumberEt=findViewById(R.id.phoneNumberEt)
        postalCodeEt=findViewById(R.id.postalCodeEt)
        addressEt=findViewById(R.id.addressEt)
        onlinePaymentBtn=findViewById(R.id.onlinePaymentBtn)
        codBtn=findViewById(R.id.codBtn)
        val purchaseDetail = intent.getParcelableExtra<PurchaseDetail>(EXTRA_KEY_DATA)
            ?: throw IllegalStateException("purchase detail cannot be null")
        val purchaseDetailView=findViewById<LinearLayout>(R.id.purchaseDetailView)
        val viewHolder = CartItemAdapter.PurchaseDetailViewHolder(purchaseDetailView)
        viewHolder.bind(
            purchaseDetail.totalPrice,
            purchaseDetail.shipping_cost,
            purchaseDetail.payable_price
        )

        val onClickListener = View.OnClickListener {
            viewModel.submitOrder(
                firstNameEt?.text.toString(),
                lastNameEt?.text.toString(),
                postalCodeEt?.text.toString(),
                phoneNumberEt?.text.toString(),
                addressEt?.text.toString(),
                if (it.id == R.id.onlinePaymentBtn) PAYMENT_METHOD_ONLINE else PAYMENT_METHOD_COD
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NikeSingleObserver<SubmitOrderResult>(compositeDisposable) {
                    override fun onSuccess(t: SubmitOrderResult) {
                        if (t.bank_gateway_url.isNotEmpty()) {
                            openUrlInCustomTab(this@ShippingActivity, t.bank_gateway_url)
                        } else {
                            startActivity(
                                Intent(
                                    this@ShippingActivity,
                                    CheckOutActivity::class.java
                                ).apply {
                                    putExtra(EXTRA_KEY_ID, t.order_id)
                                }
                            )
                        }
                        finish()

                    }
                })
        }

        onlinePaymentBtn?.setOnClickListener(onClickListener)
        codBtn?.setOnClickListener(onClickListener)
    }
}