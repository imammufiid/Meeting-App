package com.example.meeting_app.ui.scanner

import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.MeetingEntity
import com.example.meeting_app.ui.profile.ProfileState
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException

class ScannerViewModel : ViewModel() {
    private var state: SingleLiveEvent<ScannerState> = SingleLiveEvent()
    private var api = ApiConfig.instance()

    fun scan(code: String?) {
        state.value = ScannerState.IsLoading(true)
        CompositeDisposable().add(
            api.attendance(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(it.status) {
                        200 -> state.value = ScannerState.IsSuccess(it.status, it.message, it.data)
                        else -> state.value = ScannerState.IsSuccess(it.status, it.message, null)
                    }
                    state.value = ScannerState.IsLoading()
                }, {
                    val httpException = it as HttpException
                    when(httpException.code()) {
                        400 -> {
                            val message = "QRCode tidak cocok"
                            state.value = ScannerState.Error(message)
                        }
                        else -> state.value = ScannerState.Error(it.message())
                    }
                    state.value = ScannerState.IsLoading()
                })
        )
    }

    fun getState() = state
}

sealed class ScannerState() {
    data class IsLoading(var state: Boolean = false): ScannerState()
    data class IsSuccess(var status: Int?, var msg: String?, var data: MeetingEntity?): ScannerState()
    data class Error(var err: String?): ScannerState()
}