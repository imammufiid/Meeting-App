package com.example.meeting_app.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AbsenEntity(

    @field:SerializedName("qr")
    val qr: String? = null,

    @field:SerializedName("id_peserta")
    val idPeserta: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("rapat_id")
    val rapatId: String? = null,

    @field:SerializedName("tanda_tangan")
    val tandaTangan: String? = null,

    @field:SerializedName("absen")
    val absen: String? = null
) : Parcelable
