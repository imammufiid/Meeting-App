package com.example.meeting_app.ui.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ForumEntity
import kotlinx.android.synthetic.main.item_forum.view.*
import kotlin.collections.ArrayList

class ForumAdapter(val callback: ActionCallback) :
    RecyclerView.Adapter<ForumAdapter.ViewHolder>() {
    val data = ArrayList<ForumEntity>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(forum: ForumEntity) {

            itemView.user.text = forum.user?.nama
            itemView.comment.text = forum.isi
            itemView.total_likes.text = "${forum.likesCount} suka"
            itemView.total_reply.text = "${forum.totalReply} balasan"

            itemView.btn_like.setOnClickListener {
                callback.like(forum)
            }
            itemView.btn_comment.setOnClickListener {
                callback.comment(forum)
            }
        }

    }

    interface ActionCallback {
        fun like(forum: ForumEntity)
        fun comment(forum: ForumEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forum, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun setData(data: List<ForumEntity>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

}