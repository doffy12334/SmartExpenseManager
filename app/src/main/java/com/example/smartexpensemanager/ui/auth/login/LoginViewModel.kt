package com.example.smartexpensemanager.ui.auth.login

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpensemanager.data.repository.AuthRepository
import com.example.smartexpensemanager.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val authRepository: AuthRepository = AuthRepositoryImpl()
    private val _uiState = MutableStateFlow(
        LoginUiState(
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
                identifierError = null,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordRequired = false,
                errorMessage = null
            )
        }
    }

    fun onRememberSessionChanged(checked: Boolean) {
        savedState[KEY_REMEMBER] = checked
        _uiState.update {
            it.copy(rememberSession = checked)
        }
    }

    fun login() {
        val current = _uiState.value
        val validation = validateForm(current.identifier, current.password)

        if (!validation.isValid) {
            _uiState.update {
                it.copy(
                    submitted = true,
                    identifierError = validation.identifierError,
                    passwordRequired = validation.passwordRequired
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    submitted = true,
                    isLoading = true,
                    errorMessage = null
                )
            }
            val identifier = current.identifier.trim()
            val isEmail = EMAIL_REGEX.matches(identifier)

            val result = if (isEmail) {
                authRepository.loginWithEmail(
                    email = identifier,
                    password = current.password
                )
            } else {
                authRepository.loginWithPhone(
                    phone = identifier,
                    password = current.password
                )
            }

            result.onSuccess {
                Log.d("Login", "Đăng nhập thành công")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = error.message ?: "Đăng nhập thất bại"
                    )
                }
            }
        }
    }



    //Validate email /pass
    private data class ValidationResult(
        val identifierError: IdentifierError?,
        val passwordRequired: Boolean
    ) {
        val isValid: Boolean get() = identifierError == null && !passwordRequired
    }

    private fun validateForm(identifier: String, password: String): ValidationResult {
        return ValidationResult(
            identifierError = validateIdentifier(identifier),
            passwordRequired = password.isEmpty()
        )
    }
    private fun validateIdentifier(value: String): IdentifierError? {
        val id = value.trim()
        if (id.isEmpty()) return IdentifierError.REQUIRED
        val isEmail = EMAIL_REGEX.matches(id)
        val isPhone = PHONE_REGEX.matches(id.replace(PHONE_SEPARATORS, ""))
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
