package com.example.meeting_app.ui.profile

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.databinding.ActivityProfileBinding
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        init()
        setProfile()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        finish()
        return true
    }

    private fun init() {
        supportActionBar?.title = getString(R.string.title_profile)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ProfileViewModel::class.java]

        loading = ProgressDialog(this)
        _binding.btnEditProfile.setOnClickListener(this)
    }


    private fun observeViewModel() {
        viewModel.getState().observer(this, {
            handlerUIState(it)
        })

        viewModel.getUser().observe(this, {
            if (it != null) {
                _binding.etUsername.setText(it.nama)
                _binding.etEmail.setText(it.email)
                _binding.etNip.setText(it.nip)
                _binding.etInstance.setText(it.instansi)
                _binding.etAddressInstance.setText(it.alamatInstansi)
            }
        })
    }

    private fun setProfile() {
        val userData = UserPref.getUserData(this)
        viewModel.getUser(userData.idUser)
    }


    private fun handlerUIState(it: ProfileState?) {
        when (it) {
            is ProfileState.IsLoading -> showLoading(it.state)
            is ProfileState.Error -> showToast(it.err, false)
            is ProfileState.IsSuccess -> isSuccess(it.message, data = it.data)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            loading.setMessage("Loading...")
            loading.setCanceledOnTouchOutside(false)
            loading.show()
        } else {
            loading.dismiss()
        }
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

    private fun isSuccess(message: String?, state: Boolean? = true, data: UserEntity?) {
        state?.let { isSuccess ->
            if (isSuccess) {
                CustomView.customToast(this, message, true, isSuccess = true)
                _binding.etUsername.setText(data?.nama)
            } else {
                CustomView.customToast(this, message, true, isSuccess = false)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_edit_profile -> {
                val username  = _binding.etUsername.text.toString()
                val password  = _binding.etPassword.text.toString()
                if (username.isEmpty()) {
                    _binding.etUsername.error = "Field is required!"
                } else {
                    viewModel.editUser(
                        UserPref.getUserData(this).idUser,
                        username, password
                    )
                }
            }
        }
    }
}