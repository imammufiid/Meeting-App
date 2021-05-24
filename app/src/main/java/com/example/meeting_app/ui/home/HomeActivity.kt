package com.example.meeting_app.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.databinding.ActivityHomeBinding
import com.example.meeting_app.ui.detail.DetailActivity
import com.example.meeting_app.ui.login.LoginActivity
import com.example.meeting_app.ui.profile.ProfileActivity
import com.example.meeting_app.ui.scanner.ScannerActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: MettingAdapter

    companion object {
        const val ACTIVITY_NAME = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSupportActionBar()
        init()
        checkPermission()
        showDataEvent()
        setRecyclerView()
        swipeRefreshGesture()

    }

    private fun swipeRefreshGesture() {
        binding.swipeRefresh.setOnRefreshListener {
            showDataEvent()
        }
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    private fun checkLogin() {
        UserPref.getIsLoggedIn(this)?.let {
            if(!it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }

    private fun init() {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[HomeViewModel::class.java]
        viewModel.getState().observer(this, {
            handlerUIState(it)
        })

        binding.ibScan.setOnClickListener(this)
        binding.include.ibSetting.setOnClickListener(this)
        binding.include.ibFilter.setOnClickListener(this)
    }

    private fun handlerUIState(it: MeetingState?) {
        when (it) {
            is MeetingState.IsLoading -> showLoading(it.state)
            is MeetingState.Error -> showToast(it.err, false)
            is MeetingState.IsSuccess -> showToast(it.message)
        }
    }

    private fun setRecyclerView() {
        adapter = MettingAdapter(this) { event ->
            showSelectedData(event)
        }.apply {
            notifyDataSetChanged()
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter
    }

    private fun showDataEvent() {
        UserPref.getUserData(this)?.let {
            viewModel.getMeetingData(it.idUser)
        }

        viewModel.getMeetingData().observe(this, {
            if (it.isNullOrEmpty()) {
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = getString(R.string.data_not_found)
            } else {
                binding.tvMessage.visibility = View.GONE
                adapter.setData(it)
            }
        })
    }

    private fun initSupportActionBar() {
        setSupportActionBar(binding.include.toolbar)
        binding.include.titleAppbar.text = getString(R.string.home)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (
                checkSelfPermission(
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ib_setting -> {
                PopupMenu(this, binding.include.ibSetting).apply {
                    inflate(R.menu.setting_menu)
                    setOnMenuItemClickListener {
                        when(it.itemId) {
                            R.id.menu_profile -> {
                                startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                            }
                            R.id.menu_logout -> {
                                AlertDialog.Builder(this@HomeActivity).apply {
                                    setTitle(getString(R.string.confirm_logout))
                                    setMessage(getString(R.string.question_logout))
                                        .setPositiveButton(getString(R.string.yes)) { _, _->
                                            UserPref.setIsLoggedIn(this@HomeActivity, false)
                                            // viewModel.logout(UserPref.getUserData(this@HomeActivity)?.token)
                                            UserPref.clear(this@HomeActivity)
                                            Handler(mainLooper).postDelayed({
                                                startActivity(Intent(this@HomeActivity, LoginActivity::class.java).apply {
                                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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
                        return@setOnMenuItemClickListener false
                    }
                }.show()
            }
            R.id.ib_scan -> {
                startActivity(Intent(this, ScannerActivity::class.java).apply {
                    putExtra(ScannerActivity.EXTRAS_ACTIVITY, ACTIVITY_NAME)
                })
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.swipeRefresh.isRefreshing = state
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

    private fun showSelectedData(event: MeetingEntity) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRAS_DATA, event)
        })
    }
}