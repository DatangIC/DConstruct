package com.datangic.login2.ui.login

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.datangic.login2.R
import com.datangic.login2.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var loginViewModel: LoginViewModel
    lateinit var mBinding: FragmentLoginBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        mBinding.verifyCode.apply {
            setEndIconTintMode(PorterDuff.Mode.CLEAR)
            isCounterEnabled = true

        }

        return mBinding.root
    }
}