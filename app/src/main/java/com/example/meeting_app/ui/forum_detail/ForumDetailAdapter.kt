package com.example.meeting_app.ui.forum_detail

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ReplyEntity
import kotlinx.android.synthetic.main.item_meeting.view.*
import kotlinx.android.synthetic.main.item_reply.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ForumDetailAdapter: RecyclerView.Adapter<ForumDetailAdapter.ViewHolder>() {
    val data = ArrayList<ReplyEntity>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(reply: ReplyEntity) {
            itemView.username.text = reply.user?.nama
            itemView.comment.text = reply.isi
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reply, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun setEvent(event: List<ReplyEntity>) {
        data.clear()
        data.addAll(event)
        notifyDataSetChanged()
    }
}