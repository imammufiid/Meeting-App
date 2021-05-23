package com.example.meeting_app.ui.participant

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ParticipantViewModel : ViewModel() {
    private var participant = MutableLiveData<List<UserEntity>>()
    private var state: SingleLiveEvent<ParticipantState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun getParticipantJoin(token: String?, eventId: Int?) {
        state.value = ParticipantState.IsLoading(true)
        CompositeDisposable().add(
            api.getListOfParticipantJoin("Bearer $token", eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> participant.postValue(it.data)
                        else -> state.value = ParticipantState.Error(it.message)
                    }
                    state.value = ParticipantState.IsLoading()
                }, {
                    state.value = ParticipantState.Error(it.message)
                    state.value = ParticipantState.IsLoading()
                })
        )
    }

    fun getParticipantIsComing(token: String?, eventId: Int?) {
        state.value = ParticipantState.IsLoading(true)
        CompositeDisposable().add(
            api.getListOfParticipantCome("Bearer $token", eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> participant.postValue(it.data)
                        else -> state.value = ParticipantState.Error(it.message)
                    }
                    state.value = ParticipantState.IsLoading()
                }, {
                    state.value = ParticipantState.Error(it.message)
                    state.value = ParticipantState.IsLoading()
                })
        )
    }

    fun getParticipant() = participant
    fun getState() = state
}

sealed class ParticipantState() {
    data class IsLoading(var state: Boolean = false): ParticipantState()
    data class Error(var err: String?): ParticipantState()
}