package com.example.meeting_app.ui.event

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.databinding.ActivityEventBinding
import com.example.meeting_app.ui.detail.DetailActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref

class EventActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEventBinding
    private lateinit var viewModel: EventViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    override fun onResume() {
        super.onResume()
        showDataEvent()
        setRecyclerView()
    }

    private fun init() {
        binding.fabAddEvent.setOnClickListener(this)
        binding.include.ibBack.setOnClickListener(this)
        binding.include.titleAppBar.text = "Eventku"
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[EventViewModel::class.java]
        viewModel.getState().observer(this, Observer {
            handlerUIState(it)
        })
    }

    private fun handlerUIState(it: EventState?) {
        when (it) {
            is EventState.IsLoading -> showLoading(it.state)
            is EventState.Error -> showToast(it.err)
        }
    }

    private fun setRecyclerView() {
        adapter = EventAdapter { event ->
            showSelectedData(event)
        }.apply {
            notifyDataSetChanged()
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter
    }

    private fun showDataEvent() {
        UserPref.getUserData(this)?.let {
            it.token?.let { it1 -> Log.d("TOKEN", it1) }
            viewModel.getAllDataEventByCreated(it.id, it.token)
        }

        viewModel.getEvents().observe(this, Observer {
            if (it.isNullOrEmpty()) {
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = getString(R.string.data_not_found)
            } else {
                binding.tvMessage.visibility = View.GONE
                adapter.setEvent(it)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
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

    private fun showSelectedData(event: EventEntity) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRAS_DATA, event)
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fab_add_event -> {
//                startActivity(Intent(this, FormActivity::class.java))
            }
            R.id.ib_back -> {
                finish()
            }
        }
    }
}