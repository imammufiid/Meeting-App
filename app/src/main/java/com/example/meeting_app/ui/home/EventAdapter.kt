package com.example.meeting_app.ui.home

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.EventEntity
import kotlinx.android.synthetic.main.item_event.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(private val onClick: (EventEntity) -> Unit): RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    val data = ArrayList<EventEntity>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(event: EventEntity) {
            itemView.title_event.text = event.name
            itemView.date_time_event.text = "${event.startDate} / ${event.dueDate}"

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val startDateTime = LocalDateTime.parse(event.startDate, formatter)
                val dueDateTime = LocalDateTime.parse(event.dueDate, formatter)
                val currentDate = LocalDateTime.now()

                if(currentDate in startDateTime..dueDateTime) {
                    itemView.status_event.text = "Dimulai"
                } else {
                    itemView.status_event.visibility = View.GONE
                }
            } else {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val startDateTime = formatter.parse(event.startDate)
                val dueDateTime = formatter.parse(event.dueDate)

                if(Date() in startDateTime..dueDateTime) {
                    itemView.status_event.text = "Dimulai"
                }
                else {
                    itemView.status_event.visibility = View.GONE
                }
            }


            Glide.with(itemView)
                .load(event.image)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(itemView.image_event)
            itemView.setOnClickListener {
                onClick(event)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    fun setEvent(event: List<EventEntity>) {
        data.clear()
        data.addAll(event)
        notifyDataSetChanged()
    }
}