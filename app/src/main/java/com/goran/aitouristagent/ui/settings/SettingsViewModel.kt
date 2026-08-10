package com.goran.aitouristagent.ui.settings

import androidx.lifecycle.ViewModel
import com.goran.aitouristagent.data.remote.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenStore: TokenStore,
) : ViewModel() {
    val apiToken: StateFlow<String> = tokenStore.token

    fun saveApiToken(token: String) {
        tokenStore.setToken(token.trim())
    }
}
