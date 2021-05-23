package com.example.meeting_app.utils.pref

import android.content.Context
import androidx.core.content.edit
import com.example.meeting_app.data.entity.UserEntity

object UserPref {
    fun getUserData(context: Context): UserEntity? {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        return UserEntity().apply {
            username = pref.getString("USERNAME", "")
            token = pref.getString("TOKEN", "")
            firstName = pref.getString("FIRSTNAME", "")
            lastName = pref.getString("LASTNAME", "")
            email = pref.getString("EMAIL", "")
            token = pref.getString("TOKEN", "")
            id = pref.getInt("ID_USER", 0)
        }
    }

    fun setUserData(context: Context, user: UserEntity) {
        val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
        pref.edit {
            putString("USERNAME", user.username)
            putString("TOKEN", user.token)
            putString("FIRSTNAME", user.firstName)
            putString("LASTNAME", user.lastName)
            putString("EMAIL", user.email)
            putString("TOKEN", user.token)
            user.id?.let { putInt("ID_USER", it) }
        }
    }

    fun getIsLoggedIn(context: Context): Boolean? {
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