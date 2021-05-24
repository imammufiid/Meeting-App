package com.example.meeting_app.ui.detail

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ForumEntity
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.databinding.ActivityDetailBinding
import com.example.meeting_app.ui.forum_detail.ForumDetailActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref
import kotlinx.android.synthetic.main.item_meeting.view.*

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var loading: ProgressDialog
    private var dataMeeting: MeetingEntity? = null
    private lateinit var adapter: ForumAdapter

    companion object {
        const val EXTRAS_DATA = "extras_data"
        const val ACTIVITY_NAME = "DetailActivity"
        const val ASC = "asc"
        const val DESC = "desc"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setParcelable()
        observeViewModel()
        setRecyclerView()
    }

    private fun init() {

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Rapat"
        loading = ProgressDialog(this)

        binding.btnAddForum.visibility = View.VISIBLE

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]
        viewModel.getState().observer(this, Observer {
            handlerUIState(it)
        })
    }

    private fun handlerUIState(it: DetailState?) {
        when (it) {
            is DetailState.IsLoading -> showLoading(it.state)
            is DetailState.Error -> showToast(it.err, false)
        }
    }

    private fun setParcelable() {
        dataMeeting = intent.getParcelableExtra(EXTRAS_DATA)
    }

    private fun observeViewModel() {
        dataMeeting?.let {
            viewModel.getRapatById(UserPref.getUserData(this)?.idUser, dataMeeting?.idRapat)
        }

        viewModel.getMeeting().observe(this, Observer {
            if (it != null) {
                binding.titleMeeting.text = it.temaRapat
                binding.keteranganMeeting.text = it.keterangan
                binding.fromMeeting.text = it.dari
                binding.roomMeeting.text = it.ruang
                binding.dateMeeting.text = it.tglRapat
                binding.dateTimeMeeting.text = it.jamMulai
                binding.dueDateTimeMeeting.text = it.jamSelesai

                when (it.statusRapat) {
                    "0" -> binding.statusMeeting.text = "Selesai"
                    "1" -> {
                        with(binding.statusMeeting) {
                            text = "Belum Dimulai"
                            background =
                                ContextCompat.getDrawable(this@DetailActivity, R.color.colorDanger)
                        }
                    }
                    "2" -> {
                        with(binding.statusMeeting) {
                            text = "Proses"
                            background =
                                ContextCompat.getDrawable(this@DetailActivity, R.color.colorIndigo)
                        }
                    }
                    "3" -> binding.statusMeeting.text = "Disetujui\nPimpinan"
                }

                when (it.openForum) {
                    "0" -> {
                        binding.btnAddForum.visibility = View.GONE
                        binding.messageForum.visibility = View.VISIBLE
                    }
                    "1" -> {
                        viewModel.getForumByRapatId(it.idRapat)

                        if (it.statusRapat != "2") {
                            binding.btnAddForum.visibility = View.GONE
                        } else {
                            binding.btnAddForum.visibility = View.VISIBLE
                        }
                    }
                }

            }
        })

        viewModel.getForum().observe(this, {
            if (it.isNullOrEmpty()) {
                binding.messageForum.visibility = View.VISIBLE
            } else {
                binding.messageForum.visibility = View.GONE
                adapter.setData(it)
            }
        })
    }

    private fun setRecyclerView() {
        adapter = ForumAdapter(object : ForumAdapter.ActionCallback {
            override fun like(forum: ForumEntity) {
                showSelectedData(forum)
            }

            override fun comment(forum: ForumEntity) {
                startActivity(Intent(this@DetailActivity, ForumDetailActivity::class.java).apply {
                    putExtra(ForumDetailActivity.FORUM_DETAIL_EXTRAS, forum)
                })
            }
        }).apply {
            notifyDataSetChanged()
        }

        binding.rvForum.layoutManager = LinearLayoutManager(this)
        binding.rvForum.adapter = adapter
    }

    private fun showSelectedData(forum: ForumEntity) {
        Toast.makeText(this, "comment forum id ${forum.id}", Toast.LENGTH_SHORT).show()
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
        when (v?.id) {
//            R.id.layout_participant_registration -> {
//                val bundle = Bundle().apply {
//                    dataEvent?.idRapat?.let { putInt(ParticipantListDialogFragment.EVENT_ID, it) }
//                    putBoolean(ParticipantListDialogFragment.IS_COMING, false)
//                }
//                ParticipantListDialogFragment().apply {
//                    arguments = bundle
//                    show(supportFragmentManager, ParticipantListDialogFragment.TAG)
//                }
//            }
            R.id.btn_add_forum -> {
                Toast.makeText(this, "Tambah Forum", Toast.LENGTH_SHORT).show()
            }
        }

    }
}