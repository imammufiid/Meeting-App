package com.example.meeting_app.ui.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class EventViewModel: ViewModel() {
    private var events = MutableLiveData<List<EventEntity>>()
    private var state: SingleLiveEvent<EventState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun getAllDataEventByCreated(user_id: Int?, token: String?) {
        state.value = EventState.IsLoading(true)
        CompositeDisposable().add(
            api.getListOfEventCreated("Bearer $token", user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> events.postValue(it.data)
                        else -> state.value = EventState.Error(it.message)
                    }
                    state.value = EventState.IsLoading()
                }, {
                    state.value = EventState.Error(it.message)
                    state.value = EventState.IsLoading()
                })
        )
    }

    fun getEvents() = events
    fun getState() = state
}

sealed class EventState() {
    data class IsLoading(var state: Boolean = false): EventState()
    data class Error(var err: String?): EventState()
}