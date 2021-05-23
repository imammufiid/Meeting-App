package com.example.meeting_app.ui.detail

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.databinding.ActivityDetailBinding
import com.example.meeting_app.ui.participant.ParticipantListDialogFragment
import com.example.meeting_app.ui.scanner.ScannerActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref

class DetailActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var loading: ProgressDialog
    private var dataEvent: EventEntity? = null

    companion object {
        const val EXTRAS_DATA = "extras_data"
        const val ACTIVITY_NAME = "DetailActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
        setParcelable()
    }

    override fun onResume() {
        super.onResume()
        showDataById()
    }

    private fun init() {
        binding.layoutParticipantRegistration.setOnClickListener(this)
        binding.layoutParticipantCome.setOnClickListener(this)
        binding.layoutScan.setOnClickListener(this)
        binding.include.ibBack.setOnClickListener(this)
        binding.include.ibEdit.setOnClickListener(this)
        loading = ProgressDialog(this)

        binding.include.titleAppBar.text = getString(R.string.event_detail_title)

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
        dataEvent = intent.getParcelableExtra(EXTRAS_DATA)

    }

    private fun checkIfCreator(userId: Int?) {
        if(userId ==  UserPref.getUserData(this)?.id) {
            binding.include.ibEdit.visibility = View.VISIBLE
        } else {
            binding.layoutScan.visibility = View.VISIBLE
        }
    }

    private fun showDataById() {
        dataEvent?.let {
            viewModel.getEventById(UserPref.getUserData(this)?.token, it.id)
        }

        viewModel.getEvent().observe(this, Observer {
            if (it != null) {
                Glide.with(this)
                    .load(it.image)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.imageEvent)
                binding.titleEvent.text = it.name
                val startDateTime = it.startDate?.split(" ")
                val startDate = startDateTime?.get(0)
                val startTime = startDateTime?.get(1)
                binding.dateTimeEvent.text = "$startDate = $startTime"

                val dueDateTime = it.dueDate?.split(" ")
                val dueDate = dueDateTime?.get(0)
                val dueTime = dueDateTime?.get(0)
                binding.dueDateTimeEvent.text = "$dueDate - $dueTime"

                binding.descEvent.text = it.description
                binding.countParticipantRegistration.text = it.participant.toString()
                binding.countParticipantCome.text = it.participantIsComing.toString()

                checkIfCreator(it.userId)
            }
        })
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

//    override fun onSupportNavigateUp(): Boolean {
//        finish()
//        return true
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_participant_registration -> {
                val bundle = Bundle().apply {
                    dataEvent?.id?.let { putInt(ParticipantListDialogFragment.EVENT_ID, it) }
                    putBoolean(ParticipantListDialogFragment.IS_COMING, false)
                }
                ParticipantListDialogFragment().apply {
                    arguments = bundle
                    show(supportFragmentManager, ParticipantListDialogFragment.TAG)
                }
            }
            R.id.layout_participant_come -> {
                val bundle = Bundle().apply {
                    dataEvent?.id?.let { putInt(ParticipantListDialogFragment.EVENT_ID, it) }
                    putBoolean(ParticipantListDialogFragment.IS_COMING, true)
                }
                ParticipantListDialogFragment().apply {
                    arguments = bundle
                    show(supportFragmentManager, ParticipantListDialogFragment.TAG)
                }
            }
            R.id.layout_scan -> {
                startActivity(Intent(this, ScannerActivity::class.java).apply {
                    putExtra(ScannerActivity.EXTRAS_ACTIVITY, ACTIVITY_NAME)
                })
            }
            R.id.ib_back -> {
                finish()
            }
//            R.id.ib_edit -> {
//                startActivity(Intent(this, FormActivity::class.java).apply {
//                    putExtra(FormActivity.IS_UPDATE, true)
//                    putExtra(FormActivity.EXTRAS_DATA, dataEvent)
//                })
//            }
        }

    }
}