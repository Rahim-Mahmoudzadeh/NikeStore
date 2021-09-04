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
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    val viewModel: AuthViewModel by viewModel()
    val compositeDisposable = CompositeDisposable()
    var loginBtn: MaterialButton? = null
    var signUpLinkBtn:MaterialButton? = null
    var emailEt:EditText? = null
    var passwordEt:EditText? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBtn=view.findViewById(R.id.loginBtn)
        emailEt=view.findViewById(R.id.emailEt)
        passwordEt=view.findViewById(R.id.passwordEt)
        signUpLinkBtn=view.findViewById(R.id.signUpLinkBtn)
        loginBtn?.setOnClickListener {
            viewModel.login(emailEt?.text.toString(), passwordEt?.text.toString())
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : NikeCompletableObserver(compositeDisposable) {
                    override fun onComplete() {
                        requireActivity().finish()
                    }
                })
        }

        signUpLinkBtn?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, SignUpFragment())
            }.commit()
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }
}