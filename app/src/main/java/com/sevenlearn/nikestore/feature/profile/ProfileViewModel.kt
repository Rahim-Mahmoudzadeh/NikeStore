package com.sevenlearn.nikestore.feature.profile

import com.sevenlearn.nikestore.common.NikeViewModel
import com.sevenlearn.nikestore.data.TokenContainer
import com.sevenlearn.nikestore.data.repo.UserRepository

class ProfileViewModel(private val userRepository: UserRepository) : NikeViewModel() {
    val username: String
        get() = userRepository.getUserName()

    val isSignedIn: Boolean
        get() = TokenContainer.token != null

    fun signOut() = userRepository.signOut()
}