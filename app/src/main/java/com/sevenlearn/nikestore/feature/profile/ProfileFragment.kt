package com.sevenlearn.nikestore.feature.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.NikeFragment
import com.sevenlearn.nikestore.feature.auth.AuthActivity
import com.sevenlearn.nikestore.feature.favorites.FavoriteProductsActivity
import org.koin.android.ext.android.inject

class ProfileFragment : NikeFragment() {
    private val viewModel: ProfileViewModel by inject()
    var authBtn:TextView?=null
    var usernameTv:TextView?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteProductsBtn=view.findViewById<TextView>(R.id.favoriteProductsBtn)
        authBtn=view.findViewById(R.id.authBtn)
        usernameTv=view.findViewById(R.id.usernameTv)
        favoriteProductsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), FavoriteProductsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        checkAuthState()
    }

    private fun checkAuthState() {
        if (viewModel.isSignedIn) {
            authBtn?.text = getString(R.string.signOut)
            authBtn?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sign_out, 0)
            usernameTv?.text = viewModel.username
            authBtn?.setOnClickListener {
                viewModel.signOut()
                checkAuthState()
            }
        } else {
            authBtn?.text = getString(R.string.signIn)
            authBtn?.setOnClickListener {
                startActivity(Intent(requireContext(), AuthActivity::class.java))
            }
            authBtn?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sign_in, 0)
            usernameTv?.text = getString(R.string.guest_user)
        }
    }
}