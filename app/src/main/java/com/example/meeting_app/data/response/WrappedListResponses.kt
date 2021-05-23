package com.example.meeting_app.data.response

import com.google.gson.annotations.SerializedName

data class WrappedListResponses<T>(
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("data") var data: List<T>? = null
)