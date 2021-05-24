package com.example.meeting_app.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.ForumEntity
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailViewModel() : ViewModel() {
    private var meeting = MutableLiveData<MeetingEntity>()
    private var forum = MutableLiveData<List<ForumEntity>>()
    private var state: SingleLiveEvent<DetailState> = SingleLiveEvent()
    private var api = ApiConfig.instance()


    fun getRapatById(idUser: String?, eventId: String?) {
        state.value = DetailState.IsLoading(true)
        CompositeDisposable().add(
            api.getRapatById(idUser, eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> meeting.postValue(it.data)
                        else -> state.value = DetailState.Error(it.message)
                    }
                    state.value = DetailState.IsLoading()
                }, {
                    state.value = DetailState.Error(it.message)
                    state.value = DetailState.IsLoading()
                })
        )
    }

    fun getForumByRapatId(
        idRapat: String?,
        time: String? = DetailActivity.DESC,
        like: String? = DetailActivity.DESC,
        reply: String? = DetailActivity.DESC
    ) {
        state.value = DetailState.IsLoading(true)
        CompositeDisposable().add(
            api.getForumByRapatId(idRapat, time, like, reply)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> forum.postValue(it.data)
                        else -> state.value = DetailState.Error(it.message)
                    }
                    state.value = DetailState.IsLoading()
                }, {
                    state.value = DetailState.Error(it.message)
                    state.value = DetailState.IsLoading()
                })
        )
    }

    fun getMeeting() = meeting
    fun getForum() = forum
    fun getState() = state
}

sealed class DetailState() {
    data class IsLoading(var state: Boolean = false): DetailState()
    data class Error(var err: String?): DetailState()
}