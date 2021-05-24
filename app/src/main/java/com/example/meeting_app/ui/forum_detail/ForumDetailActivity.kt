package com.example.meeting_app.ui.forum_detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ForumEntity
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.databinding.ActivityForumDetailBinding
import com.example.meeting_app.ui.detail.DetailActivity
import com.example.meeting_app.utils.helper.CustomView

class ForumDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForumDetailBinding
    private lateinit var viewModel: EventViewModel
    private lateinit var adapter: ForumDetailAdapter
    private var dataForum: ForumEntity? = null

    companion object {
        const val FORUM_DETAIL_EXTRAS = "forum_detail_extras"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeViewModel()
        setRecyclerView()
        setDataFromParcelable()
    }

    private fun setDataFromParcelable() {
        binding.include.user.text = dataForum?.user?.nama
        binding.include.comment.text = dataForum?.isi
        binding.include.totalLikes.text = "${dataForum?.likesCount} suka"
        binding.include.totalReply.text = "${dataForum?.totalReply} balasan"
    }

    private fun init() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_detail_forum)

        dataForum = intent.getParcelableExtra(FORUM_DETAIL_EXTRAS)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[EventViewModel::class.java]
        viewModel.getState().observer(this, {
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
        adapter = ForumDetailAdapter().apply {
            notifyDataSetChanged()
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.getDetailForum(dataForum?.idRapat?.toInt(), dataForum?.id?.toInt())

        viewModel.getDetailForum().observe(this, {
            // set replies forum
            if (it.replies.isNullOrEmpty()) {
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = getString(R.string.replies_empty)
            } else {
                binding.tvMessage.visibility = View.GONE
                adapter.setEvent(it.replies)
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

    private fun showSelectedData(event: ForumEntity) {
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRAS_DATA, event)
        })
    }
}