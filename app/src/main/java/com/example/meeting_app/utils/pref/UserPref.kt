package com.example.meeting_app.utils.pref

import android.content.Context
import androidx.core.content.edit
import com.example.meeting_app.data.entity.UserEntity

object UserPref {
    fun getUserData(context: Context): UserEntity {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        return UserEntity().apply {
            nama = pref.getString("USERNAME", "")
            email = pref.getString("EMAIL", "")
            idUser = pref.getString("ID_USER", "")
        }
    }

    fun setUserData(context: Context, user: UserEntity) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            putString("USERNAME", user.nama)
            putString("EMAIL", user.email)
            putString("ID_USER", user.idUser)
        }
    }

    fun getIsLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        return pref.getBoolean("IS_LOGGED_IN", false)
    }

    fun setIsLoggedIn(context: Context, isLoggedIn: Boolean) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            putBoolean("IS_LOGGED_IN", isLoggedIn)
        }
    }

    fun clear(context: Context) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            clear()
        }
    }
}