package com.example.smartexpensemanager.ui.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(
            // Lấy email hoặc SĐT, lựa chọn checkbox trước đó nếu có
            identifier = savedState[KEY_IDENTIFIER] ?: "",
            rememberSession = savedState[KEY_REMEMBER] ?: false
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIdentifierChanged(value: String) {
        savedState[KEY_IDENTIFIER] = value
        _uiState.update {
            it.copy(
                identifier = value,
                identifierError = null
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordRequired = false
            )
        }
    }

    fun onRememberSessionChanged(checked: Boolean) {
        savedState[KEY_REMEMBER] = checked
        _uiState.update {
            it.copy(
                rememberSession = checked
            )
        }
    }

    fun login() {
        val current = _uiState.value
        val idError = validateIdentifier(current.identifier)
        val passError = current.password.isEmpty()

        if (idError != null || passError) {
            _uiState.update {
                it.copy(
                    submitted = true,
                    identifierError = idError,
                    passwordRequired = passError
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                submitted = true,
                isLoading = true,
                authenticationUnavailable = true
            )
        }
    }

    fun submit() = login()

    private fun validateIdentifier(value: String): IdentifierError? {
        val id = value.trim()
        if (id.isEmpty()) return IdentifierError.REQUIRED
        val isEmail = EMAIL_REGEX.matches(id)
        val isPhone = PHONE_REGEX.matches(
            id.replace(PHONE_SEPARATORS, ""))
        return if (isEmail || isPhone) null else IdentifierError.INVALID
    }

    companion object {
        private const val KEY_IDENTIFIER = "identifier"
        private const val KEY_REMEMBER = "remember"
        private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        private val PHONE_REGEX = Regex("^\\+?[0-9]{8,15}$")
        private val PHONE_SEPARATORS = Regex("[\\s().-]")
    }
}
