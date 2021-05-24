package com.example.meeting_app.ui.bottomsheetform

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meeting_app.R
import com.example.meeting_app.databinding.FragmentBottomSheetFormListDialogBinding
import com.example.meeting_app.ui.detail.DetailActivity
import com.example.meeting_app.ui.home.HomeActivity

class BottomSheetForm : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var _bind : FragmentBottomSheetFormListDialogBinding
    private var buttonListener: ButtonListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = FragmentBottomSheetFormListDialogBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind.btnAdd.setOnClickListener(this)
    }

    companion object {
        const val TAG = "bottom_sheet_form"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                val comment = _bind.comment.text.toString()
                if (buttonListener != null) buttonListener?.add(comment)
                dialog?.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is DetailActivity) {
            this.buttonListener = (activity as DetailActivity).buttonListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.buttonListener = null
    }

    interface ButtonListener {
        fun add(comment: String?)
    }
}