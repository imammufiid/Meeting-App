package com.example.meeting_app.utils.pref

import android.content.Context
import androidx.core.content.edit

object OnBoardingPref {

    fun getFirstLaunchApp(context: Context): Boolean {
        val pref = context.getSharedPreferences("LAUNCH", Context.MODE_PRIVATE)
        return pref.getBoolean("FIRST_LAUNCH", true)
    }

    fun setFirstLaunchApp(context: Context, state: Boolean) {
        val pref = context.getSharedPreferences("LAUNCH", Context.MODE_PRIVATE)
        pref.edit {
            putBoolean("FIRST_LAUNCH", state)
        }
    }
}