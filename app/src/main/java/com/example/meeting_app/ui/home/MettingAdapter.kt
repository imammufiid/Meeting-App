package com.example.meeting_app.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.MeetingEntity
import kotlinx.android.synthetic.main.item_meeting.view.*
import kotlin.collections.ArrayList

class MettingAdapter(val context: Context, private val onClick: (MeetingEntity) -> Unit) :
    RecyclerView.Adapter<MettingAdapter.ViewHolder>() {
    val data = ArrayList<MeetingEntity>()
    lateinit var startTime : String
    lateinit var dueTime : String

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(meeting: MeetingEntity) {
            itemView.keterangan.text = meeting.keterangan
            itemView.title_meeting.text = meeting.temaRapat

            if (meeting.tglRapat != null) {
                val splitDate = splitDateTime(meeting.tglRapat, "-")
                itemView.date_event.text = "${meeting.hari}, ${splitDate[2]}-${splitDate[1]}-${splitDate[0]}"
            }

            if (meeting.jamMulai != null) {
                val splitTime = splitDateTime(meeting.jamMulai, ":")
                 startTime = "${splitTime[0]}:${splitTime[1]}"
            }

            if (meeting.jamSelesai != null) {
                val splitTime = splitDateTime(meeting.jamSelesai, ":")
                dueTime = "${splitTime[0]}:${splitTime[1]}"
            }


            itemView.date_time_event.text =
                "$startTime - $dueTime"

            when (meeting.statusRapat) {
                "0" -> {
                    itemView.status_event.text = "Selesai"
                    itemView.status_event.background = context.resources.getDrawable(R.color.colorPrimary, null)
                }
                "1" -> {
                    itemView.status_event.text = "Belum Dimulai"
                    itemView.status_event.background = context.resources.getDrawable(R.color.colorDanger, null)
                }
                "2" -> {
                    itemView.status_event.text = "Proses"
                    itemView.status_event.background = context.resources.getDrawable(R.color.colorIndigo, null)
                }
                "3" -> itemView.status_event.text = "Disetujui\nPimpinan"
            }

            itemView.setOnClickListener {
                onClick(meeting)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meeting, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun setData(data: List<MeetingEntity>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    private fun splitDateTime(data: String, delimiter: String): List<String> {
        return data.split(delimiter)
    }
}