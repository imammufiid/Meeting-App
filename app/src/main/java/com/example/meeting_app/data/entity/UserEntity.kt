package com.example.meeting_app.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntity(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("is_active")
	val isActive: String? = null,

	@field:SerializedName("level")
	val level: String? = null,

	@field:SerializedName("jabatan")
	val jabatan: String? = null,

	@field:SerializedName("date_created")
	val dateCreated: String? = null,

	@field:SerializedName("pangkat")
	val pangkat: String? = null,

	@field:SerializedName("id_user")
	var idUser: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("fax_instansi")
	val faxInstansi: String? = null,

	@field:SerializedName("nama")
	var nama: String? = null,

	@field:SerializedName("nip")
	val nip: String? = null,

	@field:SerializedName("alamat_instansi")
	val alamatInstansi: String? = null,

	@field:SerializedName("telepon_instansi")
	val teleponInstansi: String? = null,

	@field:SerializedName("email")
	var email: String? = null,

	@field:SerializedName("instansi")
	val instansi: String? = null
) : Parcelable
