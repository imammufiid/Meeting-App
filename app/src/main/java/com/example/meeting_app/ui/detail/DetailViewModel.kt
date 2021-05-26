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
    private var forums = MutableLiveData<List<ForumEntity>>()
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
        time: String? = "",
        like: String? = "",
        reply: String? = ""
    ) {
        state.value = DetailState.IsLoadingProgressBar(true)
        CompositeDisposable().add(
            api.getForumByRapatId(idRapat, time, like, reply)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> forums.postValue(it.data)
                        else -> state.value = DetailState.Error(it.message)
                    }
                    state.value = DetailState.IsLoadingProgressBar()
                }, {
                    state.value = DetailState.Error(it.message)
                    state.value = DetailState.IsLoadingProgressBar()
                })
        )
    }

    fun likeForum(idForum: String?, idUser: String?) {
        state.value = DetailState.IsLoading(true)
        CompositeDisposable().add(
            api.likeForum(idForum, idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        201 -> state.value = DetailState.LikeForum(it.message, it.data)
                        else -> state.value = DetailState.Error(it.message)
                    }
                    state.value = DetailState.IsLoading()
                }, {
                    state.value = DetailState.Error(it.message)
                    state.value = DetailState.IsLoading()
                })
        )
    }

    fun addForum(idRapat: String?, idUser: String?, isi: String?) {
        state.value = DetailState.IsLoadingProgressBar(true)
        CompositeDisposable().add(
            api.addForum(idRapat, idUser, isi)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        201 -> state.value = DetailState.AddForum(it.message, it.data)
                        else -> state.value = DetailState.Error(it.message)
                    }
                    state.value = DetailState.IsLoadingProgressBar()
                }, {
                    state.value = DetailState.Error(it.message)
                    state.value = DetailState.IsLoadingProgressBar()
                })
        )
    }

    fun getMeeting() = meeting
    fun getForums() = forums
    fun getState() = state
}

sealed class DetailState() {
    data class LikeForum(var message: String?, var data: ForumEntity?): DetailState()
    data class AddForum(var message: String?, var data: ForumEntity?): DetailState()
    data class IsLoading(var state: Boolean = false): DetailState()
    data class IsLoadingProgressBar(var state: Boolean = false): DetailState()
    data class Error(var err: String?): DetailState()
}