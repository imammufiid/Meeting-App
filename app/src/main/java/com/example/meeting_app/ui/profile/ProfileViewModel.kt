package com.example.meeting_app.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.ui.detail.DetailState
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.http.Multipart
import java.io.IOException

class ProfileViewModel : ViewModel() {
    private var user = MutableLiveData<UserEntity>()
    private var state: SingleLiveEvent<ProfileState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun getUser(idUser: String?) {
        state.value = ProfileState.IsLoading(true)
        CompositeDisposable().add(
            api.getUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.status) {
                        200 -> user.postValue(it.data)
                        else -> state.value = ProfileState.Error(it.message)
                    }
                    state.value = ProfileState.IsLoading()
                }, {
                    val httpException = it as HttpException
                    when (httpException.code()) {
                        404 -> {
                            val message = "Data tidak ditemukan"
                            state.value = ProfileState.Error(message)
                        }
                        else -> state.value = ProfileState.Error(it.message())
                    }
                    state.value = ProfileState.IsLoading()
                })
        )
    }

    fun editUser(
        idUser: String?,
        name: RequestBody?,
        nip: RequestBody?,
        jabatan: RequestBody?,
        pangkat: RequestBody?,
        instansi: RequestBody?,
        addressInstansi: RequestBody?,
        telp: RequestBody?,
        fax: RequestBody?,
        password: RequestBody?,
        image: MultipartBody.Part? = null
    ) {
        state.value = ProfileState.IsLoading(true)
        CompositeDisposable().add(
            api.editProfile(
                idUser, name, nip, jabatan, pangkat, instansi,
                addressInstansi, telp, fax, password, image
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.status) {
                        201 -> state.value = ProfileState.IsSuccess(it.message, it.data)
                        else -> state.value = ProfileState.Error(it.message)
                    }
                    state.value = ProfileState.IsLoading()
                }, {
                    when (it) {
                        is IOException -> state.value = ProfileState.Error("Network Error")
                        is HttpException -> {
                            val code = it.code()
                            val errorResponse = it.message
                            state.value = ProfileState.Error("Error $errorResponse")
                        }
                        else -> state.value = ProfileState.Error(it.message)
                    }
//                    state.value = ProfileState.Error(it.message)
                    state.value = ProfileState.IsLoading()
                })
        )
    }

    fun getUser() = user
    fun getState() = state
}

sealed class ProfileState() {
    data class IsLoading(var state: Boolean = false) : ProfileState()
    data class IsSuccess(var message: String?, var data: UserEntity? = null) : ProfileState()
    data class Error(var err: String?) : ProfileState()
}