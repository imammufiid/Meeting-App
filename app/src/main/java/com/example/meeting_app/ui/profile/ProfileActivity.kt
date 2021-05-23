package com.example.meeting_app.ui.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.meeting_app.R
import com.example.meeting_app.databinding.ActivityProfileBinding
import com.example.meeting_app.ui.login.LoginActivity
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
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        finish()
        return true
    }

    private fun init() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ProfileViewModel::class.java]
        viewModel.getState().observer(this, Observer {
            handlerUIState(it)
        })
        loading = ProgressDialog(this)
        _binding.btnLogout.setOnClickListener(this)
    }

    private fun setProfile() {
        val userData = UserPref.getUserData(this)
        Log.d("USERDATA", userData.toString())
        userData?.let { user ->
            _binding.tvUsername.text = user.username
            _binding.tvEmail.text = user.email
            _binding.tvFullname.text = "${user.firstName} ${user.lastName}"

            viewModel.setCountEvent(user.id, user.token)
            viewModel.getCountEvent().observe(this, Observer {
                _binding.tvEventCreated.text = it.countEventCreated.toString()
                _binding.tvEventJoin.text = it.countEventJoin.toString()
            })
        }

    }


    private fun handlerUIState(it: ProfileState?) {
        when (it) {
            is ProfileState.IsLoading -> showLoading(it.state)
            is ProfileState.Error -> showToast(it.err, false)
            is ProfileState.IsSuccess -> showToast(it.message)
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.confirm_logout))
                    setMessage(getString(R.string.question_logout))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.logout(UserPref.getUserData(this@ProfileActivity)?.token)
                            UserPref.clear(this@ProfileActivity)
                            UserPref.setIsLoggedIn(this@ProfileActivity, false)
                            Handler(mainLooper).postDelayed({
                                startActivity(Intent(
                                    this@ProfileActivity,
                                    LoginActivity::class.java
                                ).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                })
                                finish()
                            }, 1000)
                        }
                        .setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                }.show()

            }
        }
    }
}