package com.example.meeting_app.api

import com.example.meeting_app.data.entity.CountEventEntity
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.data.response.MessageResponse
import com.example.meeting_app.data.response.WrappedListResponses
import com.example.meeting_app.data.response.WrappedResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiServices {
    // LOGIN
    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Observable<WrappedResponse<UserEntity>>

    // REGISTRATION
    @FormUrlEncoded
    @POST("auth/register")
    fun register(
        @Field("username") username: String?,
        @Field("firstname") firstName: String?,
        @Field("lastname") lastName: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("access_id") accessId: Int?,
        @Field("role_id") roleId: Int?
    ): Observable<WrappedResponse<UserEntity>>

    // LOGOUT
    @FormUrlEncoded
    @POST("auth/logout")
    fun logout(
        @Header("Authorization") token: String?,
        @Field("user_id") userId: Int?
    ): Observable<WrappedResponse<EventEntity>>

    // GET EVENTS JOIN
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("event/participant/events")
    fun getListOfEventJoin(
        @Header("Authorization") token: String?,
        @Field("user_id") userId: Int?
    ): Observable<WrappedListResponses<EventEntity>>

    // GET LIST PARTICIPANT JOIN
    @FormUrlEncoded
    @POST("event/participant")
    fun getListOfParticipantJoin(
        @Header("Authorization") token: String?,
        @Field("event_id") eventId: Int?
    ): Observable<WrappedListResponses<UserEntity>>

    // GET LIST PARTICIPANT JOIN
    @FormUrlEncoded
    @POST("event/participant/come")
    fun getListOfParticipantCome(
        @Header("Authorization") token: String?,
        @Field("event_id") eventId: Int?
    ): Observable<WrappedListResponses<UserEntity>>

    // GET EVENTS CREATED
    @GET("event/")
    fun getListOfEventCreated(
        @Header("Authorization") token: String?,
        @Query("user_id") userId: Int?
    ): Observable<WrappedListResponses<EventEntity>>

    // GET SHOW EVENT
    @GET("event/show")
    fun getEventById(
        @Header("Authorization") token: String?,
        @Query("id") id: Int?
    ): Observable<WrappedResponse<EventEntity>>

    // GET SEARCH EVENT
    @Headers("Accept: application/json")
    @GET("event/search")
    fun getEventBySearch(
        @Header("Authorization") token: String?,
        @Query("query") query: String?,
        @Query("user_id") userId: Int?
    ): Observable<WrappedListResponses<EventEntity>>

    // GET SEARCH EVENT
    @Headers("Accept: application/json")
    @GET("event/participant/count")
    fun getCountEvent(
        @Header("Authorization") token: String?,
        @Query("user_id") userId: Int?
    ): Observable<WrappedResponse<CountEventEntity>>

    // GET EVENTS CREATED
    @DELETE("event/{id}")
    fun deleteListOfEventCreated(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Observable<MessageResponse>

    // CREATE EVENT
    @Multipart
    @POST("event/create")
    fun insertEvent(
        @HeaderMap token: Map<String, String>?,
        @Part("user_id") userId: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("start_date") startDate: RequestBody?,
        @Part("due_date") dueDate: RequestBody?,
        @Part("capasity") capacity: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Observable<MessageResponse>

    // UPDATE EVENT
    @Multipart
    @POST("event/update")
    fun updateEvent(
        @HeaderMap token: Map<String, String>?,
        @Part("id") eventId: RequestBody?,
        @Part("user_id") userId: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("start_date") startDate: RequestBody?,
        @Part("due_date") dueDate: RequestBody?,
        @Part("capasity") capacity: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Observable<MessageResponse>

    // GET LIST PARTICIPANT JOIN
    @FormUrlEncoded
    @POST("event/scan")
    fun scanQRCode(
        @Header("Authorization") token: String?,
        @Field("user_id") userId: Int?,
        @Field("code_event") codeEvent: String?
    ): Observable<WrappedResponse<EventEntity>>

    // GET LIST PARTICIPANT JOIN
    @FormUrlEncoded
    @POST("event/registration")
    fun scanRegistrationEvent(
        @Header("Authorization") token: String?,
        @Field("user_id") userId: Int?,
        @Field("code_event") codeEvent: String?
    ): Observable<WrappedResponse<EventEntity>>
}