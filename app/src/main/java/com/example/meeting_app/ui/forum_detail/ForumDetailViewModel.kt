package com.example.meeting_app.ui.forum_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.ForumEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class EventViewModel: ViewModel() {
    private var forumDetail = MutableLiveData<ForumEntity>()
    private var state: SingleLiveEvent<ReplyState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun getDetailForum(idRapat: Int?, idForum: Int?) {
        state.value = EventState.IsLoading(true)
        CompositeDisposable().add(
            api.getDetailForum(idRapat, idForum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> forumDetail.postValue(it.data)
                        else -> state.value = EventState.Error(it.message)
                    }
                    state.value = EventState.IsLoading()
                }, {
                    state.value = EventState.Error(it.message)
                    state.value = EventState.IsLoading()
                })
        )
    }

    fun getDetailForum() = forumDetail
    fun getState() = state
}

sealed class ReplyState() {
    data class ReplyForum(var message: String?, var data: ReplyEntity?): ReplyState()
    data class IsLoading(var state: Boolean = false): ReplyState()
    data class Error(var err: String?): ReplyState()
}