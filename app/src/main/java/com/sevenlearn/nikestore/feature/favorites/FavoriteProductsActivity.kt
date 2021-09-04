package com.sevenlearn.nikestore.feature.favorites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.EXTRA_KEY_DATA
import com.sevenlearn.nikestore.common.NikeActivity
import com.sevenlearn.nikestore.data.Product
import com.sevenlearn.nikestore.feature.product.ProductDetailActivity

import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class FavoriteProductsActivity : NikeActivity(),
    FavoriteProductsAdapter.FavoriteProductEventListener {
    val viewModel: FavoriteProductsViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_products)
        val helpBtn=findViewById<Button>(R.id.helpBtn)
        val favoriteProductsRv=findViewById<RecyclerView>(R.id.favoriteProductsRv)
        val emptyStateMessageTv=findViewById<TextView>(R.id.emptyStateMessageTv)
        helpBtn.setOnClickListener {
            Snackbar.make(it, R.string.favorites_help_message, Snackbar.LENGTH_LONG).show()
        }

        viewModel.productsLiveData.observe(this) {
            if (it.isNotEmpty()) {
                favoriteProductsRv.layoutManager =
                    LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                favoriteProductsRv.adapter =
                    FavoriteProductsAdapter(it as MutableList<Product>, this, get())
            } else {
                showEmptyState(R.layout.view_default_empty_state)
                emptyStateMessageTv.text = getString(R.string.favorites_empty_state_message)
            }
        }

    }

    override fun onClick(product: Product) {
        startActivity(Intent(this, ProductDetailActivity::class.java).apply {
            putExtra(EXTRA_KEY_DATA, product)
        })
    }

    override fun onLongClick(product: Product) {
        viewModel.removeFromFavorites(product)
    }
}