package com.goran.aitouristagent.data.remote

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "ai_tourist_agent_prefs"
private const val KEY_API_TOKEN = "api_token"

@Singleton
class TokenStore @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _token = MutableStateFlow(prefs.getString(KEY_API_TOKEN, null).orEmpty())
    val token: StateFlow<String> = _token

    fun currentToken(): String = _token.value

    fun setToken(value: String) {
        prefs.edit { putString(KEY_API_TOKEN, value) }
        _token.value = value
    }
}
