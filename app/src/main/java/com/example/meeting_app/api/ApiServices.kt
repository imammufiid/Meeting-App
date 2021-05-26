package com.example.meeting_app.api

import com.example.meeting_app.data.entity.*
import com.example.meeting_app.data.response.MessageResponse
import com.example.meeting_app.data.response.WrappedListResponses
import com.example.meeting_app.data.response.WrappedResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

interface ApiServices {
    // LOGIN
    @FormUrlEncoded
    @POST("peserta/login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Observable<WrappedResponse<UserEntity>>

    // GET MEETING DATA
    @GET("peserta/rapat/{id_user}")
    fun getMeeting(
        @Path("id_user") idUser: String?,
        @Query("status") status: Int?
    ): Observable<WrappedListResponses<MeetingEntity>>

    // GET DETAIL FORUM
    @GET("peserta/forum/{id_rapat}/{id}")
    fun getDetailForum(
        @Path("id_rapat") idRapat: Int?,
        @Path("id") id: Int?
    ): Observable<WrappedResponse<ForumEntity>>

    // GET SHOW MEETING
    @GET("peserta/rapat/{id_user}/{id_rapat}")
    fun getRapatById(
        @Path("id_user") idUser: String?,
        @Path("id_rapat") idRapat: String?,
    ): Observable<WrappedResponse<MeetingEntity>>

    // GET FORUM
    @GET("peserta/forum/{id_rapat}")
    fun getForumByRapatId(
        @Path("id_rapat") idRapat: String?,
        @Query("filter_time") filterTime: String?,
        @Query("filter_like") filterLike: String?,
        @Query("filter_reply") filterReply: String?,
    ): Observable<WrappedListResponses<ForumEntity>>

    // LIKE FORUM
    @FormUrlEncoded
    @POST("peserta/forumlike")
    fun likeForum(
        @Field("id_forum") idForum: String?,
        @Field("id_user") idUser: String?
    ): Observable<WrappedResponse<ForumEntity>>

    // ADD FORUM
    @FormUrlEncoded
    @POST("peserta/forum")
    fun addForum(
        @Field("id_rapat") idRapat: String?,
        @Field("id_user") idUser: String?,
        @Field("isi") isi: String?
    ): Observable<WrappedResponse<ForumEntity>>

    // REPLY FORUM
    @FormUrlEncoded
    @POST("peserta/forumreply")
    fun replyForum(
        @Field("id_forum") idForum: String?,
        @Field("id_rapat") idRapat: String?,
        @Field("id_user") idUser: String?,
        @Field("isi") isi: String?
    ): Observable<WrappedResponse<ReplyEntity>>

    // GET USER
    @GET("peserta/user/{id_user}")
    fun getUser(
        @Path("id_user") idUser: String?,
    ): Observable<WrappedResponse<UserEntity>>

    // EDIT PROFILE
    @FormUrlEncoded
    @PUT("peserta/user/{id_user}")
    fun editProfile(
        @Path("id_user") idUser: String?,
        @Field("nama") name: String?,
        @Field("password") password: String?,
    ): Observable<WrappedResponse<UserEntity>>

    // ATTENDANCE
    @FormUrlEncoded
    @PUT("peserta/absen")
    fun attendance(
        @Field("code") code: String?,
    ): Observable<WrappedResponse<MeetingEntity>>
}