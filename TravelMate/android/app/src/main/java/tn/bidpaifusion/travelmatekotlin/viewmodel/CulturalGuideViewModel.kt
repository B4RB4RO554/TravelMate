package tn.bidpaifusion.travelmatekotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tn.bidpaifusion.travelmatekotlin.data.api.RetrofitInstance
import tn.bidpaifusion.travelmatekotlin.data.api.TranslationRequest

data class CulturalGuideState(
    val isLoading: Boolean = false,
    val translatedText: String? = null,
    val error: String? = null,
    val sourceLanguage: String = "en",
    val targetLanguage: String = "fr",
    val inputText: String = ""
)

class CulturalGuideViewModel : ViewModel() {
    private val api = RetrofitInstance.translationApi

    private val _state = MutableStateFlow(CulturalGuideState())
    val state = _state.asStateFlow()

    fun updateInputText(text: String) {
        _state.value = _state.value.copy(inputText = text)
    }

    fun updateSourceLanguage(lang: String) {
        _state.value = _state.value.copy(sourceLanguage = lang)
    }

    fun updateTargetLanguage(lang: String) {
        _state.value = _state.value.copy(targetLanguage = lang)
    }

    fun translate() {
        val currentState = _state.value
        if (currentState.inputText.isBlank()) {
            _state.value = currentState.copy(error = "Please enter text to translate")
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, error = null)
            try {
                val request = TranslationRequest(
                    text = currentState.inputText,
                    source = currentState.sourceLanguage,
                    target = currentState.targetLanguage
                )
                val response = api.translate(request)
                _state.value = currentState.copy(
                    isLoading = false,
                    translatedText = response.translated,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isLoading = false,
                    error = "Translation failed: ${e.message}"
                )
            }
        }
    }

    fun swapLanguages() {
        val current = _state.value
        _state.value = current.copy(
            sourceLanguage = current.targetLanguage,
            targetLanguage = current.sourceLanguage,
            inputText = current.translatedText ?: current.inputText,
            translatedText = null
        )
    }
}
