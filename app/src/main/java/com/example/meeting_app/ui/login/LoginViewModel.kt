package com.example.meeting_app.ui.login

import androidx.lifecycle.ViewModel
import com.example.meeting_app.api.ApiConfig
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginViewModel : ViewModel() {
    private var state: SingleLiveEvent<AuthState> = SingleLiveEvent()
    private val api = ApiConfig.instance()
    fun login(email: String?, password: String?) {
        state.value = AuthState.IsLoading(true)
        CompositeDisposable().add(
            api.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when (it.status) {
                        200 -> state.value = it.data?.let { data -> AuthState.IsSuccess(data) }
                        else -> state.value = AuthState.IsFailed(it.message)
                    }
                    state.value = AuthState.IsLoading()
                }, {
                    state.value = AuthState.IsFailed(it.message)
                    state.value = AuthState.IsLoading()
                })
        )
    }

    fun loginValidate(
        email: String?,
        password: String?,
    ): Boolean {
        state.value = AuthState.Reset
        if (email != null) {
            if (email.isEmpty()) {
                state.value = AuthState.Error("Email Tidak Boleh Kosong!")
                return false
            }
        }

        if (password != null) {
            if (password.isEmpty()) {
                state.value = AuthState.Error("Password Tidak Boleh Kosong")
                return false
            }
        }

        return true
    }

    fun getState() = state
}


sealed class AuthState {
    data class ShowToast(var message: String?) : AuthState()
    data class IsLoading(var state: Boolean? = false) : AuthState()
    data class Error(var err: String?) : AuthState()
    data class IsSuccess(var user: UserEntity?) : AuthState()
    data class IsFailed(var message: String? = null) : AuthState()
    data class LoginValidation(
        var email: String? = null,
        var password: String? = null
    ) : AuthState()

    object Reset : AuthState()
}