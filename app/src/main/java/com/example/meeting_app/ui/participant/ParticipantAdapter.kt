package com.example.meeting_app.ui.participant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.UserEntity
import kotlinx.android.synthetic.main.item_participant.view.*

class ParticipantAdapter :
    RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    var data = ArrayList<UserEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantAdapter.ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(user: UserEntity) {
            itemView.tv_username.text = user.username
        }
    }

    fun setParticipant(user: List<UserEntity>) {
        data.clear()
        data.addAll(user)
        notifyDataSetChanged()
    }
}