package tn.bidpaifusion.travelmatekotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.api.DistressSignalRequest
import tn.bidpaifusion.travelmatekotlin.data.api.DistressSignalResponse
import tn.bidpaifusion.travelmatekotlin.data.api.RetrofitInstance

data class DistressState(
    val isLoading: Boolean = false,
    val activeSignal: DistressSignalResponse? = null,
    val error: String? = null,
    val success: String? = null
)

class DistressViewModel : ViewModel() {
    private val api = RetrofitInstance.distressApi

    private val _state = MutableStateFlow(DistressState())
    val state = _state.asStateFlow()

    fun sendDistressSignal(token: String, lat: Double, lon: Double, message: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val request = DistressSignalRequest(
                    latitude = lat,
                    longitude = lon,
                    message = message
                )
                val response = api.sendDistressSignal("Bearer $token", request)
                _state.value = DistressState(
                    isLoading = false,
                    activeSignal = response,
                    success = "Distress signal sent! Your location has been shared."
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to send distress signal: ${e.message}"
                )
            }
        }
    }

    fun deactivateSignal(token: String, signalId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                api.deactivateDistressSignal("Bearer $token", signalId)
                _state.value = DistressState(
                    isLoading = false,
                    success = "Distress signal deactivated"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to deactivate: ${e.message}"
                )
            }
        }
    }

    fun loadActiveSignals(token: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val signals = api.getActiveDistressSignals("Bearer $token")
                val activeSignal = signals.firstOrNull { it.isActive }
                _state.value = _state.value.copy(
                    isLoading = false,
                    activeSignal = activeSignal
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load signals: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, success = null)
    }
}
