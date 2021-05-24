package com.example.meeting_app.ui.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.databinding.ActivityLoginBinding
import com.example.meeting_app.ui.home.HomeActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSupportActionBar()
        init()
        checkLogin()
    }

    private fun checkLogin() {
        UserPref.getIsLoggedIn(this)?.let {
            if(it) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

    }

    private fun init() {
        binding.btnLogin.setOnClickListener(this)
        loading = ProgressDialog(this)
        
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]
        viewModel.getState().observer(this, {
            handleUIState(it)
        })
    }

    private fun handleUIState(authState: AuthState?) {
        when(authState) {
            is AuthState.Reset -> {
                setEmailError(null)
                setPasswordError(null)
            }
            is AuthState.IsLoading -> isLoading(authState.state)
            is AuthState.ShowToast -> showToast(authState.message)
            is AuthState.IsSuccess -> isSuccess(authState.user)
            is AuthState.IsFailed -> {
                isLoading(false)
                authState.message?.let { message -> showToast(message) }
            }
            is AuthState.Error -> {
                isLoading(false)
                showToast(authState.err, false)
            }
            is AuthState.LoginValidation -> {
                authState.email?.let {
                    setEmailError(it)
                }
                authState.password?.let {
                    setPasswordError(it)
                }
            }
        }
    }

    private fun initSupportActionBar() {
        supportActionBar?.hide()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_login -> {
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                if(viewModel.loginValidate(email, password)) {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun setPasswordError(err: String?) {
        binding.etPassword.error = err
    }

    private fun setEmailError(err: String?) {
        binding.etEmail.error = err
    }

    private fun isSuccess(user: UserEntity?) {
        user?.let { user -> UserPref.setUserData(this, user) }
        UserPref.setIsLoggedIn(this, true)
        CustomView.customToast(this, getString(R.string.login_success), true, isSuccess = true)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 2000)

    }

    private fun showToast(message: String?, state: Boolean? = true) {
        state?.let { isSuccess ->
            if (isSuccess) {
                CustomView.customToast(this, message, true, isSuccess = true)
            } else {
                CustomView.customToast(this, message, true, isSuccess = false)
            }
        }
    }

    private fun isLoading(state: Boolean?) {
        state?.let { state ->
            if(state) {
                loading.setMessage(getString(R.string.loading))
                loading.setCanceledOnTouchOutside(false)
                loading.show()
            } else {
                loading.dismiss()
            }
        }

    }
}