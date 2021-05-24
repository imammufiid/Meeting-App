package com.example.meeting_app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel:ViewModel() {
    private var meeting = MutableLiveData<List<MeetingEntity>>()
    private var state: SingleLiveEvent<MeetingState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun getMeetingData(idUser: String?) {
        state.value = MeetingState.IsLoading(true)
        CompositeDisposable().add(
            api.getMeeting(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> meeting.postValue(it.data)
                        else -> state.value = MeetingState.Error(it.message)
                    }
                    state.value = MeetingState.IsLoading()
                }, {
                    state.value = MeetingState.Error(it.message)
                    state.value = MeetingState.IsLoading()
                })
        )
    }

    fun logout(token: String?, userId: Int? = null) {
        state.value = MeetingState.IsLoading(true)
        CompositeDisposable().add(
            api.logout("Bearer $token", userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> state.value = MeetingState.IsSuccess(it.message)
                        else -> state.value = MeetingState.Error(it.message)
                    }
                    state.value = MeetingState.IsLoading()
                }, {
                    state.value = MeetingState.Error(it.message)
                    state.value = MeetingState.IsLoading()
                })
        )
    }


    fun getMeetingData() = meeting
    fun getState() = state
}
sealed class MeetingState() {
    data class IsLoading(var state: Boolean = false): MeetingState()
    data class IsSuccess(var message: String?): MeetingState()
    data class Error(var err: String?): MeetingState()
}