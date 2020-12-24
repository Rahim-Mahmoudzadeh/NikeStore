package com.sevenlearn.nikestore.feature.product

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.NikeActivity
import com.sevenlearn.nikestore.common.formatPrice
import com.sevenlearn.nikestore.data.Comment
import com.sevenlearn.nikestore.feature.ProductDetailViewModel
import com.sevenlearn.nikestore.services.ImageLoadingService
import com.sevenlearn.nikestore.view.scroll.ObservableScrollViewCallbacks
import com.sevenlearn.nikestore.view.scroll.ScrollState
import kotlinx.android.synthetic.main.activity_product_detail.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ProductDetailActivity : NikeActivity() {
    val productDetailViewModel: ProductDetailViewModel by viewModel { parametersOf(intent.extras) }
    val imageLoadingService: ImageLoadingService by inject()
    val commentAdapter = CommentAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        productDetailViewModel.productLiveData.observe(this) {
            imageLoadingService.load(productIv, it.image)
            titleTv.text = it.title
            previousPriceTv.text = formatPrice(it.previous_price)
            currentPriceTv.text = formatPrice(it.price)
            toolbarTitleTv.text = it.title
        }

        productDetailViewModel.commentsLiveData.observe(this) {
            Timber.i(it.toString())
            commentAdapter.comments = it as ArrayList<Comment>
            if (it.size > 3) {
                viewAllCommentsBtn.visibility = View.VISIBLE
            }
        }

        productDetailViewModel.progressBarLiveData.observe(this) {

        }

        initViews()

    }

    fun initViews() {
        commentsRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        commentsRv.adapter = commentAdapter
        commentsRv.isNestedScrollingEnabled = false

        productIv.post {
            val productIvHeight = productIv.height
            val toolbar = toolbarView
            observableScrollView.addScrollViewCallbacks(object : ObservableScrollViewCallbacks {
                override fun onScrollChanged(
                    scrollY: Int,
                    firstScroll: Boolean,
                    dragging: Boolean
                ) {
                    Timber.i("productIv height is -> $productIvHeight")
                    toolbar.alpha = scrollY.toFloat() / productIvHeight.toFloat()
                }

                override fun onDownMotionEvent() {
                }

                override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {
                }

            })
        }

    }
}