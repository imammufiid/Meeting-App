package com.example.meeting_app.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeetingEntity(

	@field:SerializedName("hari")
	val hari: String? = null,

	@field:SerializedName("jam_mulai")
	val jamMulai: String? = null,

	@field:SerializedName("keterangan")
	val keterangan: String? = null,

	@field:SerializedName("tgl_rapat")
	val tglRapat: String? = null,

	@field:SerializedName("jam_selesai")
	val jamSelesai: String? = null,

	@field:SerializedName("ruang")
	val ruang: String? = null,

	@field:SerializedName("id_rapat")
	val idRapat: String? = null,

	@field:SerializedName("status_rapat")
	val statusRapat: String? = null,

	@field:SerializedName("notulen_id")
	val notulenId: String? = null,

	@field:SerializedName("tema_rapat")
	val temaRapat: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("open_forum")
	val openForum: String? = null,

	@field:SerializedName("jenis_id")
	val jenisId: String? = null,

	@field:SerializedName("dari")
	val dari: String? = null,

	@field:SerializedName("absen")
	val absen: AbsenEntity? = null
) : Parcelable
