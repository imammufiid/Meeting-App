package com.example.meeting_app.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.ForumEntity
import kotlinx.android.synthetic.main.item_forum.view.*
import okhttp3.internal.format
import kotlin.collections.ArrayList

class ForumAdapter(val context: Context, val callback: ActionCallback) :
    RecyclerView.Adapter<ForumAdapter.ViewHolder>() {
    val data = ArrayList<ForumEntity>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(forum: ForumEntity) {

            itemView.user.text = forum.user?.nama
            itemView.comment.text = forum.isi

            // likes
            if (forum.likesCount == null) {
                itemView.total_likes.text = "0 suka"
            } else {
                itemView.total_likes.text = "${forum.likesCount} suka"
            }

            // reply
            if (forum.totalReply == null) {
                itemView.total_reply.text = "0 balasan"
            } else {
                itemView.total_reply.text = "${forum.totalReply} balasan"
            }

            // date time
            val dateTime = forum.waktu?.split(" ")
            val date = dateTime?.get(0)?.split("-")
            val time = dateTime?.get(1)?.split(":") as ArrayList
            time.removeAt(time.lastIndex)
            itemView.date_time.text =
                "${date?.reversed()?.joinToString("-")}/${time.joinToString(":")}"

            // likes
            itemView.btn_like.background =
                context.resources.getDrawable(R.drawable.ic_favorite_border, null)
            itemView.btn_like.isEnabled = true
            if (forum.likes != null) {
                if (forum.likes.contains(forum.idUser)) {
                    itemView.btn_like.background =
                        context.resources.getDrawable(R.drawable.ic_favorite, null)
                    itemView.btn_like.isEnabled = false
                }
            }

            // listener
            itemView.btn_like.setOnClickListener {
                itemView.btn_like.background =
                    context.resources.getDrawable(R.drawable.ic_favorite, null)
                itemView.btn_like.isEnabled = false
                callback.like(forum, adapterPosition)
            }
            itemView.btn_comment.setOnClickListener {
                callback.comment(forum)
            }
        }

    }

    interface ActionCallback {
        fun like(forum: ForumEntity, position: Int)
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

    fun changeItem(item: ForumEntity, index: Int) {
        this.data[index] = item
        notifyItemChanged(index)
    }

    fun addOneItem(item: ForumEntity, index: Int) {
        this.data.add(item)
        notifyDataSetChanged()
    }

}