package com.example.meeting_app.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventEntity(

	@field:SerializedName("image_url")
	val image: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("url_qr_code")
	val urlQrCode: String? = null,

	@field:SerializedName("event_id")
	val eventId: Int? = null,

	@field:SerializedName("createBy_userId")
	val userId: Int? = null,

	@field:SerializedName("capasity")
	val capasity: Int? = 0,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("due_date")
	val dueDate: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("is_user_come")
	val isUserCome: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("start_date")
	val startDate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("participant")
	val participant: Int? = 0,

	@field:SerializedName("participant_is_coming")
	val participantIsComing: Int? = 0,
): Parcelable
