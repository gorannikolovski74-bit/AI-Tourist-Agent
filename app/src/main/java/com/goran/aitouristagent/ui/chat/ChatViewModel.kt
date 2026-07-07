package com.goran.aitouristagent.ui.chat

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goran.aitouristagent.data.remote.ImageUtils
import com.goran.aitouristagent.data.repository.ChatRepository
import com.goran.aitouristagent.domain.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val tripId: String = checkNotNull(savedStateHandle["tripId"])

    val messages: StateFlow<List<ChatMessage>> = chatRepository.observeMessages(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        viewModelScope.launch { chatRepository.refreshFromServer(tripId) }
    }

    fun sendMessage(text: String, imageBitmap: Bitmap?) {
        if (text.isBlank() && imageBitmap == null) return
        viewModelScope.launch {
            _isSending.value = true
            val imageBase64 = imageBitmap?.let {
                withContext(Dispatchers.Default) { ImageUtils.compressToBase64Jpeg(it) }
            }
            chatRepository.sendMessage(tripId, text, imageBase64)
                .onFailure { _errorMessage.value = it.message ?: "Испраќањето не успеа" }
            _isSending.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
