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
import retrofit2.HttpException

class ProfileViewModel: ViewModel() {
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
                    when(it.status) {
                        200 -> user.postValue(it.data)
                        else -> state.value = ProfileState.Error(it.message)
                    }
                    state.value = ProfileState.IsLoading()
                }, {
                    val httpException = it as HttpException
                    when(httpException.code()) {
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

    fun editUser(idUser: String?, username: String?, password: String?) {
        state.value = ProfileState.IsLoading(true)
        CompositeDisposable().add(
            api.editProfile(idUser, username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        201 -> state.value = ProfileState.IsSuccess(it.message, it.data)
                        else -> state.value = ProfileState.Error(it.message)
                    }
                    state.value = ProfileState.IsLoading()
                }, {
                    state.value = ProfileState.Error(it.message)
                    state.value = ProfileState.IsLoading()
                })
        )
    }

    fun getUser() = user
    fun getState() = state
}

sealed class ProfileState() {
    data class IsLoading(var state: Boolean = false): ProfileState()
    data class IsSuccess(var message: String?, var data: UserEntity? = null): ProfileState()
    data class Error(var err: String?): ProfileState()
}