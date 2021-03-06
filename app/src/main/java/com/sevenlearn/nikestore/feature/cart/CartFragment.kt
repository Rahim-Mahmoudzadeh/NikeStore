package com.sevenlearn.nikestore.feature.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_DATA
import com.sevenlearn.nikestore.common.NikeCompletableObserver
import com.sevenlearn.nikestore.common.NikeFragment
import com.sevenlearn.nikestore.data.CartItem
import com.sevenlearn.nikestore.feature.auth.AuthActivity
import com.sevenlearn.nikestore.feature.product.ProductDetailActivity
import com.sevenlearn.nikestore.feature.shipping.ShippingActivity
import com.sevenlearn.nikestore.services.ImageLoadingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class CartFragment : NikeFragment(), CartItemAdapter.CartItemViewCallbacks {
    val viewModel: CartViewModel by viewModel()
    var cartItemAdapter: CartItemAdapter? = null
    val imageLoadingService: ImageLoadingService by inject()
    val compositeDisposable = CompositeDisposable()
    var cartItemsRv:RecyclerView?=null
    var payBtn: ExtendedFloatingActionButton?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cartItemsRv=view.findViewById(R.id.cartItemsRv)
        payBtn=view.findViewById(R.id.payBtn)
        viewModel.progressBarLiveData.observe(viewLifecycleOwner) {
            setProgressIndicator(it)
        }

        viewModel.cartItemsLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            cartItemsRv?.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            cartItemAdapter =
                CartItemAdapter(it as MutableList<CartItem>, imageLoadingService, this)
            cartItemsRv?.adapter = cartItemAdapter
        }

        viewModel.purchaseDetailLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            cartItemAdapter?.let { adapter ->
                adapter.purchaseDetail = it
                adapter.notifyItemChanged(adapter.cartItems.size)
            }
        }

        viewModel.emptyStateLiveData.observe(viewLifecycleOwner) {
            var emptyStateRootView : FrameLayout?=null
            if (it.mustShow) {
                val emptyState = showEmptyState(R.layout.view_cart_empty_state)
                var emptyStateMessageTv:TextView?=null
                var emptyStateCtaBtn: MaterialButton?=null
                emptyState?.let { view ->
                    emptyStateMessageTv=view.findViewById(R.id.emptyStateMessageTv)
                    emptyStateCtaBtn=view.findViewById(R.id.emptyStateCtaBtn)
                    emptyStateRootView=view.findViewById(R.id.emptyStateRootView)
                    emptyStateMessageTv?.text = getString(it.messageResId)
                    emptyStateCtaBtn?.visibility =
                        if (it.mustShowCallToActionButton) View.VISIBLE else View.GONE
                   emptyStateCtaBtn?.setOnClickListener {
                        startActivity(Intent(requireContext(), AuthActivity::class.java))
                    }
                }
            } else
                emptyStateRootView?.visibility = View.GONE
        }

        payBtn?.setOnClickListener {
            startActivity(Intent(requireContext(), ShippingActivity::class.java).apply {
                putExtra(EXTRA_KEY_DATA, viewModel.purchaseDetailLiveData.value)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    override fun onRemoveCartItemButtonClick(cartItem: CartItem) {
        viewModel.removeItemFromCart(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.removeCartItem(cartItem)
                }
            })
    }

    override fun onIncreaseCartItemButtonClick(cartItem: CartItem) {
        viewModel.increaseCartItemCount(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.increaseCount(cartItem)
                }
            })
    }

    override fun onDecreaseCartItemButtonClick(cartItem: CartItem) {
        viewModel.decreaseCartItemCount(cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                override fun onComplete() {
                    cartItemAdapter?.decreaseCount(cartItem)
                }
            })
    }

    override fun onProductImageClick(cartItem: CartItem) {
        startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, cartItem.product)
        })
    }
}