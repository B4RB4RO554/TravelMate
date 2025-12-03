package tn.bidpaifusion.travelmatekotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.api.RetrofitInstance

data class CurrencyState(
    val isLoading: Boolean = false,
    val convertedAmount: Double? = null,
    val rate: Double? = null,
    val error: String? = null,
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val amount: String = "1.0"
)

class CurrencyViewModel : ViewModel() {
    private val api = RetrofitInstance.currencyApi

    private val _state = MutableStateFlow(CurrencyState())
    val state = _state.asStateFlow()

    fun updateFromCurrency(currency: String) {
        _state.value = _state.value.copy(fromCurrency = currency)
    }

    fun updateToCurrency(currency: String) {
        _state.value = _state.value.copy(toCurrency = currency)
    }

    fun updateAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount)
    }

    fun convertCurrency() {
        val currentState = _state.value
        val amountValue = currentState.amount.toDoubleOrNull()

        if (amountValue == null || amountValue <= 0) {
            _state.value = currentState.copy(error = "Please enter a valid amount")
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, error = null)
            try {
                val response = api.convertCurrency(
                    from = currentState.fromCurrency,
                    to = currentState.toCurrency,
                    amount = amountValue
                )
                _state.value = currentState.copy(
                    isLoading = false,
                    convertedAmount = response.converted,
                    rate = response.rate,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isLoading = false,
                    error = "Failed to convert: ${e.message}"
                )
            }
        }
    }

    fun swapCurrencies() {
        val current = _state.value
        _state.value = current.copy(
            fromCurrency = current.toCurrency,
            toCurrency = current.fromCurrency,
            convertedAmount = null,
            rate = null
        )
    }
}
