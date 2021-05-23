package com.example.meeting_app.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.CountEventEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ProfileViewModel: ViewModel() {
    private var count = MutableLiveData<CountEventEntity>()
    private var state: SingleLiveEvent<ProfileState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun setCountEvent(user_id: Int?, token: String?) {
        state.value = ProfileState.IsLoading(true)
        CompositeDisposable().add(
            api.getCountEvent("Bearer $token", user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> count.postValue(it.data)
                        else -> state.value = ProfileState.Error(it.message)
                    }
                    state.value = ProfileState.IsLoading()
                }, {
                    state.value = ProfileState.Error(it.message)
                    state.value = ProfileState.IsLoading()
                })
        )
    }

    fun logout(token: String?, userId: Int? = null) {
        state.value = ProfileState.IsLoading(true)
        CompositeDisposable().add(
            api.logout("Bearer $token", userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> state.value = ProfileState.IsSuccess(it.message)
                        else -> state.value = ProfileState.Error(it.message)
                    }
                    state.value = ProfileState.IsLoading()
                }, {
                    state.value = ProfileState.Error(it.message)
                    state.value = ProfileState.IsLoading()
                })
        )
    }

    fun getCountEvent() = count
    fun getState() = state
}

sealed class ProfileState() {
    data class IsLoading(var state: Boolean = false): ProfileState()
    data class IsSuccess(var message: String?): ProfileState()
    data class Error(var err: String?): ProfileState()
}