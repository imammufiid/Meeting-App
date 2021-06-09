package com.example.meeting_app.ui.detail

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ForumEntity
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.databinding.ActivityDetailBinding
import com.example.meeting_app.ui.bottomsheetform.BottomSheetForm
import com.example.meeting_app.ui.forum_detail.ForumDetailActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref
import kotlinx.android.synthetic.main.forum_box.*
import kotlinx.android.synthetic.main.item_meeting.view.*

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var loading: ProgressDialog
    private var dataMeeting: MeetingEntity? = null
    private lateinit var adapter: ForumAdapter
    private var itemPositionLike: Int? = 0
    private var qrWebUrl: String? = ""
    private var qrMobileUrl: String? = ""

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
        supportActionBar?.title = getString(R.string.title_meeting_detail)
        loading = ProgressDialog(this)

        binding.btnAddForum.setOnClickListener(this)
        binding.btnFilter.setOnClickListener(this)
        binding.qrWeb.setOnClickListener(this)
        binding.qrMobile.setOnClickListener(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[DetailViewModel::class.java]
        viewModel.getState().observer(this, {
            handlerUIState(it)
        })
    }

    private fun handlerUIState(it: DetailState?) {
        when (it) {
            is DetailState.IsLoading -> showLoading(it.state)
            is DetailState.IsLoadingProgressBar -> showLoadingProgressBar(it.state)
            is DetailState.Error -> showToast(it.err, false)
            is DetailState.LikeForum -> likeForum(it.message, it.data)
            is DetailState.AddForum -> addForum(it.message, it.data)
        }
    }

    private fun addForum(message: String?, data: ForumEntity?) {
        CustomView.customToast(this, message, true, isSuccess = true)

        if (data != null) {
            val likes = data.likesCount ?: 0
            val replies = data.totalReply ?: 0

            val newData = ForumEntity(
                id = data.id,
                isi = data.isi,
                idUser = data.idUser,
                idRapat = data.idRapat,
                user = UserEntity(nama = data.user?.nama),
                waktu = data.waktu,
                likesCount = likes,
                totalReply = replies
            )
            adapter.addOneItem(newData, 0)
        }

    }

    private fun likeForum(message: String?, oldData: ForumEntity?) {
        CustomView.customToast(this, message, true, isSuccess = true)

        if (oldData != null) {
            val newData = ForumEntity(
                id = oldData.id,
                isi = oldData.isi,
                idUser = oldData.idUser,
                idRapat = oldData.idRapat,
                user = UserEntity(nama = oldData.user?.nama),
                likesCount = oldData.likesCount,
                totalReply = oldData.totalReply,
                likes = oldData.likes,
                waktu = oldData.waktu
            )
            itemPositionLike?.let { position -> adapter.changeItem(newData, position) }
        }

    }

    private fun setParcelable() {
        dataMeeting = intent.getParcelableExtra(EXTRAS_DATA)
    }

    private fun observeViewModel() {
        dataMeeting?.let {
            viewModel.getRapatById(UserPref.getUserData(this).idUser, dataMeeting?.idRapat)
        }

        viewModel.getMeeting().observe(this, {
            if (it != null) {
                binding.titleMeeting.text = it.temaRapat
                binding.keteranganMeeting.text = it.keterangan
                binding.fromMeeting.text = it.dari
                binding.roomMeeting.text = it.ruang

                val date = it.tglRapat?.split("-")?.reversed() as ArrayList
                binding.dateMeeting.text = date.joinToString("-")
                binding.dateTimeMeeting.text = it.jamMulai
                binding.dueDateTimeMeeting.text = it.jamSelesai

                // show qr code
                Glide.with(this)
                    .load(it.absen?.qrWeb)
                    .into(binding.qrWeb)
                qrWebUrl = it.absen?.qrWeb

                Glide.with(this)
                    .load(it.absen?.qrMobile)
                    .into(binding.qrMobile)
                qrMobileUrl = it.absen?.qrMobile

                when (it.statusRapat) {
                    "0" -> binding.statusMeeting.text = getString(R.string.done)
                    "2" -> {
                        with(binding.statusMeeting) {
                            text = getString(R.string.process)
                            background =
                                ContextCompat.getDrawable(this@DetailActivity, R.color.colorIndigo)
                        }
                    }
                    else -> {
                        with(binding.statusMeeting) {
                            text = getString(R.string.not_yet_start)
                            background =
                                ContextCompat.getDrawable(this@DetailActivity, R.color.colorDanger)
                        }
                    }
                }

                when (it.openForum) {
                    "0" -> {
                        binding.btnAddForum.visibility = View.GONE
                        binding.btnFilter.visibility = View.GONE
                        binding.messageForum.visibility = View.VISIBLE
                    }
                    "1" -> {
                        viewModel.getForumByRapatId(it.idRapat)

                        if (it.statusRapat != "2") {
                            binding.btnAddForum.visibility = View.GONE
                            binding.btnFilter.visibility = View.GONE
                        } else {
                            binding.btnAddForum.visibility = View.VISIBLE
                            binding.btnFilter.visibility = View.VISIBLE
                        }
                    }
                }

            }
        })

        viewModel.getForums().observe(this, {
            if (it.isNullOrEmpty()) {
                binding.messageForum.visibility = View.VISIBLE
            } else {
                binding.messageForum.visibility = View.GONE
                adapter.setData(it)
            }
        })
    }

    private fun setRecyclerView() {
        adapter = ForumAdapter(this, object : ForumAdapter.ActionCallback {
            override fun like(forum: ForumEntity, position: Int) {
                itemPositionLike = position
                viewModel.likeForum(forum.id, UserPref.getUserData(this@DetailActivity).idUser)
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

    private fun showLoading(state: Boolean) {
        if (state) {
            loading.setMessage("Loading...")
            loading.setCanceledOnTouchOutside(false)
            loading.show()
        } else {
            loading.dismiss()
        }
    }

    private fun showLoadingProgressBar(state: Boolean) {
        if (state) {
            binding.progressBarForum.visibility = View.VISIBLE
        } else {
            binding.progressBarForum.visibility = View.GONE
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

    internal var buttonListener: BottomSheetForm.ButtonListener =
        object : BottomSheetForm.ButtonListener {
            override fun add(comment: String?) {
                viewModel.addForum(
                    dataMeeting?.idRapat,
                    UserPref.getUserData(this@DetailActivity).idUser,
                    comment
                )
            }
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_forum -> {
                BottomSheetForm().show(
                    supportFragmentManager, BottomSheetForm.TAG
                )
            }
            R.id.btn_filter -> {
                PopupMenu(this, binding.btnFilter).apply {
                    inflate(R.menu.menu_filter)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.latest -> viewModel.getForumByRapatId(
                                dataMeeting?.idRapat,
                                time = DESC
                            )
                            R.id.oldest -> viewModel.getForumByRapatId(
                                dataMeeting?.idRapat,
                                time = ASC
                            )
                            R.id.like_a_most -> viewModel.getForumByRapatId(
                                dataMeeting?.idRapat,
                                like = DESC
                            )
                            R.id.like_a_little -> viewModel.getForumByRapatId(
                                dataMeeting?.idRapat,
                                like = ASC
                            )
                            R.id.comment_a_most -> viewModel.getForumByRapatId(
                                dataMeeting?.idRapat,
                                reply = DESC
                            )
                            R.id.comment_a_little -> viewModel.getForumByRapatId(
                                dataMeeting?.idRapat,
                                reply = ASC
                            )
                        }
                        return@setOnMenuItemClickListener false
                    }
                }.show()
            }
            R.id.qr_web -> {
                ImagePopup(this).apply {
                    backgroundColor = Color.BLACK
                    isFullScreen = true
                    isImageOnClickClose = true
                    initiatePopupWithGlide(qrWebUrl)
                }.viewPopup()
            }
            R.id.qr_mobile -> {
                ImagePopup(this).apply {
                    backgroundColor = Color.BLACK
                    isFullScreen = true
                    isImageOnClickClose = true
                    initiatePopupWithGlide(qrMobileUrl)
                }.viewPopup()
            }
        }

    }
}