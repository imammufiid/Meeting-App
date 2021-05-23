package com.example.meeting_app.utils.helper


import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.meeting_app.R

object CustomView {

    fun customToast(context: Context?, message: String?, isShort: Boolean? = true, isSuccess: Boolean? = false) {
        val layoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.custom_toast, null)
        val textMessage = view.findViewById(R.id.text) as TextView
        val icon = view.findViewById(R.id.icon) as ImageView

        isSuccess?.let {
            if(!it) {
                icon.setImageResource(R.drawable.ic_warning)
            }
        }

        textMessage.text = message

        isShort?.let {
            val toast = Toast(context).apply {
                setGravity(Gravity.CENTER, 0, 0)
                setView(view)
            }
            if(it) {
                toast.duration = Toast.LENGTH_SHORT
            } else {
                toast.duration = Toast.LENGTH_LONG
            }
            toast.show()
        }
    }
}