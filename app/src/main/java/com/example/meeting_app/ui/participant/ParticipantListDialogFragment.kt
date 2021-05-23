package com.example.meeting_app.ui.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.meeting_app.R
import com.example.meeting_app.databinding.FragmentParticipantListDialogListDialogBinding
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref


class ParticipantListDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentParticipantListDialogListDialogBinding
    private lateinit var viewModel: ParticipantViewModel
    private var adapter: ParticipantAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantListDialogListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        showDataParticipant()
        showRecyclerView()
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

//        dialog.setOnShowListener {
//            val bottomSheet = (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
//            val behavior = BottomSheetBehavior.from(bottomSheet)
//
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
//            })
//        }

        // Do something with your dialog like setContentView() or whatever
//        return dialog
//    }


    private fun init() {
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ParticipantViewModel::class.java]
        viewModel.getState().observer(this, Observer {
            handlerUIState(it)
        })
    }

    private fun handlerUIState(it: ParticipantState?) {
        when(it) {
            is ParticipantState.IsLoading -> showLoading(it.state)
            is ParticipantState.Error -> showToast(it.err, false)
        }
    }

    private fun showDataParticipant() {
        val isComing = arguments?.getBoolean(IS_COMING, false)
        if(isComing != null) {
            if(isComing) {
                viewModel.getParticipantIsComing(
                    context?.let { UserPref.getUserData(it)?.token },
                    arguments?.getInt(EVENT_ID, 0)
                )
            } else {
                viewModel.getParticipantJoin(
                    context?.let { UserPref.getUserData(it)?.token },
                    arguments?.getInt(EVENT_ID, 0)
                )
            }
        }
        viewModel.getParticipant().observe(this, Observer {
            if (it.isNullOrEmpty()) {
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = resources.getString(R.string.not_have_participant)
            } else {
                adapter?.setParticipant(it)
            }
        })
    }

    private fun showRecyclerView() {
        adapter = ParticipantAdapter().apply {
            notifyDataSetChanged()
        }

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
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
                CustomView.customToast(context, message, true, isSuccess = true)
            } else {
                CustomView.customToast(context, message, true, isSuccess = false)
            }
        }
    }

    companion object {
        const val TAG = "BottomSheet"
        const val EVENT_ID = "event_id"
        const val IS_COMING = "is_coming"
    }
}