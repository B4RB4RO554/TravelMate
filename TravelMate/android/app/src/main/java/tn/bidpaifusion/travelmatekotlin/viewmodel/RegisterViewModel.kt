package tn.bidpaifusion.travelmatekotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.models.RegisterRequest
import tn.bidpaifusion.travelmatekotlin.data.repository.AuthRepository

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state = _state.asStateFlow()

    fun register(request: RegisterRequest) {
        _state.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val response = repository.registerUser(request)
                _state.value = if (response.isSuccessful) {
                    RegisterState.Success
                } else {
                    RegisterState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("Error: ${e.message}")
            }
        }
    }
}
