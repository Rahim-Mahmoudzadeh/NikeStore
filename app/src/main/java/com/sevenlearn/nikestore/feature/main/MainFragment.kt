package com.sevenlearn.nikestore.feature.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_DATA
import com.sevenlearn.nikestore.common.NikeFragment
import com.sevenlearn.nikestore.common.convertDpToPixel
import com.sevenlearn.nikestore.data.Product
import com.sevenlearn.nikestore.feature.product.ProductDetailActivity
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainFragment : NikeFragment(), ProductListAdapter.OnProductClickListener {
    val mainViewModel: MainViewModel by viewModel()
    val productListAdapter: ProductListAdapter by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latestProductsRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        latestProductsRv.adapter = productListAdapter
        productListAdapter.onProductClickListener = this

        mainViewModel.productsLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            productListAdapter.products = it as ArrayList<Product>
        }

        mainViewModel.progressBarLiveData.observe(viewLifecycleOwner) {
            setProgressIndicator(it)
        }

        mainViewModel.bannersLiveData.observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            val bannerSliderAdapter = BannerSliderAdapter(this, it)
            bannerSliderViewPager.adapter = bannerSliderAdapter
            val viewPagerHeight = (((bannerSliderViewPager.measuredWidth - convertDpToPixel(
                32f,
                requireContext()
            )) * 173) / 328).toInt()

            val layoutParams = bannerSliderViewPager.layoutParams
            layoutParams.height = viewPagerHeight
            bannerSliderViewPager.layoutParams = layoutParams

            sliderIndicator.setViewPager2(bannerSliderViewPager)

        }

    }

    override fun onProductClick(product: Product) {
        startActivity(Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, product)
        })
    }
}