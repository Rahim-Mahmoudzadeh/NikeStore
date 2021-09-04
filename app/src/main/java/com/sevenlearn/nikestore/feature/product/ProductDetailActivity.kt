package com.sevenlearn.nikestore.feature.product

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_ID
import com.sevenlearn.nikestore.common.NikeActivity
import com.sevenlearn.nikestore.common.NikeCompletableObserver
import com.sevenlearn.nikestore.common.formatPrice
import com.sevenlearn.nikestore.data.Comment
import com.sevenlearn.nikestore.feature.ProductDetailViewModel
import com.sevenlearn.nikestore.feature.product.comment.CommentListActivity
import com.sevenlearn.nikestore.services.ImageLoadingService
import com.sevenlearn.nikestore.view.NikeImageView
import com.sevenlearn.nikestore.view.NikeToolbar
import com.sevenlearn.nikestore.view.scroll.ObservableScrollView
import com.sevenlearn.nikestore.view.scroll.ObservableScrollViewCallbacks
import com.sevenlearn.nikestore.view.scroll.ScrollState
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ProductDetailActivity : NikeActivity() {
    val productDetailViewModel: ProductDetailViewModel by viewModel { parametersOf(intent.extras) }
    val imageLoadingService: ImageLoadingService by inject()
    val commentAdapter = CommentAdapter()
    val compositeDisposable = CompositeDisposable()
    var commentsRv:RecyclerView?=null
    var productIv:NikeImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
         productIv=findViewById(R.id.productIv)
        val titleTv=findViewById<TextView>(R.id.titleTv)
        commentsRv=findViewById(R.id.commentsRv)
        val previousPriceTv=findViewById<TextView>(R.id.previousPriceTv)
        val currentPriceTv=findViewById<TextView>(R.id.currentPriceTv)
        val toolbarTitleTv=findViewById<TextView>(R.id.toolbarTitleTv)
        val viewAllCommentsBtn=findViewById<MaterialButton>(R.id.viewAllCommentsBtn)
        productDetailViewModel.productLiveData.observe(this) {
            imageLoadingService.load(productIv!!, it.image)
            titleTv.text = it.title
            previousPriceTv.text = formatPrice(it.previous_price)
            previousPriceTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            currentPriceTv.text = formatPrice(it.price)
            toolbarTitleTv.text = it.title
        }

        productDetailViewModel.progressBarLiveData.observe(this) {
            setProgressIndicator(it)
        }

        productDetailViewModel.commentsLiveData.observe(this) {
            Timber.i(it.toString())
            commentAdapter.comments = it as ArrayList<Comment>
            if (it.size > 3) {
                viewAllCommentsBtn.visibility = View.VISIBLE
                viewAllCommentsBtn.setOnClickListener {
                    startActivity(Intent(this, CommentListActivity::class.java).apply {
                        putExtra(EXTRA_KEY_ID, productDetailViewModel.productLiveData.value!!.id)
                    })
                }
            }
        }

        initViews()

    }

    fun initViews() {
        commentsRv?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        commentsRv?.adapter = commentAdapter
        commentsRv?.isNestedScrollingEnabled = false
        val observableScrollView=findViewById<ObservableScrollView>(R.id.observableScrollView)
        val addToCartBtn=findViewById<Button>(R.id.addToCartBtn)
        productIv?.post {
            val toolbarView=findViewById<NikeToolbar>(R.id.toolbarView)
            val productIvHeight = productIv?.height
            val toolbar = toolbarView
            val productImageView = productIv
            observableScrollView.addScrollViewCallbacks(object : ObservableScrollViewCallbacks {
                override fun onScrollChanged(
                    scrollY: Int,
                    firstScroll: Boolean,
                    dragging: Boolean
                ) {
                    Timber.i("productIv height is -> $productIvHeight")
                    toolbar.alpha = scrollY.toFloat() / productIvHeight!!.toFloat()
                    productImageView?.translationY = scrollY.toFloat() / 2
                }

                override fun onDownMotionEvent() {
                }

                override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {
                }

            })
        }

        addToCartBtn.setOnClickListener {
            productDetailViewModel.onAddToCartBtn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        showSnackBar(getString(R.string.success_addToCart))
                    }
                })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}