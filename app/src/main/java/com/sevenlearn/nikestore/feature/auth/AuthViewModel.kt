package com.sevenlearn.nikestore.feature.auth

import com.sevenlearn.nikestore.common.NikeViewModel
import com.sevenlearn.nikestore.data.repo.UserRepository
import io.reactivex.Completable

class AuthViewModel(private val userRepository: UserRepository) : NikeViewModel() {

    fun login(email: String, password: String): Completable {
        progressBarLiveData.value = true
        return userRepository.login(email, password).doFinally {
            progressBarLiveData.postValue(false)
        }
    }

    fun signUp(email: String, password: String): Completable {
        progressBarLiveData.value = true
        return userRepository.signUp(email, password).doFinally {
            progressBarLiveData.postValue(false)
        }
    }
}