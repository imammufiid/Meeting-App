package com.example.meeting_app.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForumEntity(

	@field:SerializedName("likes_count")
	val likesCount: Int? = null,

	@field:SerializedName("waktu")
	val waktu: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("id_rapat")
	val idRapat: String? = null,

	@field:SerializedName("id_user")
	val idUser: String? = null,

	@field:SerializedName("user")
	val user: UserEntity? = null,

	@field:SerializedName("isi")
	val isi: String? = null,

	@field:SerializedName("suka")
	val suka: String? = null,

	@field:SerializedName("likes")
	val likes: List<String?>? = null,

	@field:SerializedName("total_reply")
	val totalReply: Int? = null,

	@field:SerializedName("reply")
	val replies: List<ReplyEntity>? = null
) : Parcelable
