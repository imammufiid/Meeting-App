package com.example.meeting_app.ui.scanner

import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ScannerViewModel : ViewModel() {
    private var state: SingleLiveEvent<ScannerState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun scan(token: String?, user_id: Int?, codeEvent: String?) {
        state.value = ScannerState.IsLoading(true)
        CompositeDisposable().add(
            api.scanQRCode("Bearer $token", user_id, codeEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> state.value = ScannerState.IsSuccess(it.status, it.message, it.data)
                        404 -> state.value = ScannerState.IsSuccess(it.status, it.message, it.data)
                        else -> state.value = ScannerState.Error(it.message)
                    }
                    state.value = ScannerState.IsLoading()
                }, {
                    state.value = ScannerState.Error(it.message)
                    state.value = ScannerState.IsLoading()
                })
        )
    }

    fun registration(token: String?, user_id: Int?, codeEvent: String?) {
        state.value = ScannerState.IsLoading(true)
        CompositeDisposable().add(
            api.scanRegistrationEvent("Bearer $token", user_id, codeEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> state.value = ScannerState.IsSuccess(it.status, it.message, it.data)
                        404 -> state.value = ScannerState.IsSuccess(it.status, it.message, it.data)
                        else -> state.value = ScannerState.Error(it.message)
                    }
                    state.value = ScannerState.IsLoading()
                }, {
                    state.value = ScannerState.Error(it.message)
                    state.value = ScannerState.IsLoading()
                })
        )
    }
    fun getState() = state
}

sealed class ScannerState() {
    data class IsLoading(var state: Boolean = false): ScannerState()
    data class IsSuccess(var status: Int?, var msg: String?, var data: EventEntity?): ScannerState()
    data class Error(var err: String?): ScannerState()
}