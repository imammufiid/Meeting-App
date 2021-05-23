package com.example.meeting_app.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel:ViewModel() {
    private var events = MutableLiveData<List<EventEntity>>()
    private var state: SingleLiveEvent<EventState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun getAllDataByJoin(user_id: Int?, token: String?) {
        state.value = EventState.IsLoading(true)
        CompositeDisposable().add(
            api.getListOfEventJoin("Bearer $token", user_id)
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

    fun getEventsBySearch(token: String?, query: String?, userId: Int?) {
        state.value = EventState.IsLoading(true)
        CompositeDisposable().add(
            api.getEventBySearch("Bearer $token", query, userId)
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

    fun logout(token: String?, userId: Int? = null) {
        state.value = EventState.IsLoading(true)
        CompositeDisposable().add(
            api.logout("Bearer $token", userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> state.value = EventState.IsSuccess(it.message)
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
    data class IsSuccess(var message: String?): EventState()
    data class Error(var err: String?): EventState()
}