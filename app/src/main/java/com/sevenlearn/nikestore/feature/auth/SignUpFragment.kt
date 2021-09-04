package com.sevenlearn.nikestore.feature.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.sevenlearn.nikestore.R
import com.sevenlearn.nikestore.common.NikeCompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class SignUpFragment : Fragment() {
    val compositeDisposable = CompositeDisposable()
    val viewModel: AuthViewModel by inject()
    var loginLinkBtn: MaterialButton? = null
    var signUpBtn: MaterialButton? = null
    var emailEt: EditText? = null
    var passwordEt: EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginLinkBtn=view.findViewById(R.id.loginLinkBtn)
        emailEt=view.findViewById(R.id.emailEt)
        passwordEt=view.findViewById(R.id.passwordEt)
        signUpBtn=view.findViewById(R.id.signUpBtn)
        signUpBtn?.setOnClickListener {
            viewModel.signUp(emailEt?.text.toString(), passwordEt?.text.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        requireActivity().finish()
                    }
                })
        }

        loginLinkBtn?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, LoginFragment())
            }.commit()
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}