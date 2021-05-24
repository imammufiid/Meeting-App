package com.example.meeting_app.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReplyEntity(

	@field:SerializedName("id_forum")
	val idForum: String? = null,

	@field:SerializedName("waktu")
	val waktu: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null,

	@field:SerializedName("isi")
	val isi: String? = null,

	@field:SerializedName("user")
	val user: UserEntity? = null
) : Parcelable
