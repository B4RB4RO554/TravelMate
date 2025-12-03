package tn.bidpaifusion.travelmatekotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.models.LoginRequest
import tn.bidpaifusion.travelmatekotlin.data.repository.AuthRepository

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val userId: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    fun login(emailOrUsername: String, password: String) {
        _state.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response = repository.loginUser(LoginRequest(emailOrUsername, password))
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    _state.value = LoginState.Success(data.token, data.userId)
                } else {
                    _state.value = LoginState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error("Network error: ${e.message}")
            }
        }
    }
}
