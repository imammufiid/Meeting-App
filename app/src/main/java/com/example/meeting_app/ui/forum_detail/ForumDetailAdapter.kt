package com.example.meeting_app.ui.forum_detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ReplyEntity
import kotlinx.android.synthetic.main.item_reply.view.*
import kotlinx.android.synthetic.main.item_reply.view.comment
import kotlinx.android.synthetic.main.item_reply.view.date_time
import kotlin.collections.ArrayList

class ForumDetailAdapter: RecyclerView.Adapter<ForumDetailAdapter.ViewHolder>() {
    val data = ArrayList<ReplyEntity>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(reply: ReplyEntity) {
            itemView.username.text = reply.user?.nama
            itemView.comment.text = reply.isi

            val dateTime = reply.waktu?.split(" ")
            val date = dateTime?.get(0)?.split("-")
            val time = dateTime?.get(1)?.split(":") as ArrayList
            time.removeAt(time.lastIndex)
            itemView.date_time.text =
                "${date?.reversed()?.joinToString("-")}/${time.joinToString(":")}"
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

    fun addOneItem(item: ReplyEntity) {
        this.data.add(item)
        notifyDataSetChanged()
    }
}