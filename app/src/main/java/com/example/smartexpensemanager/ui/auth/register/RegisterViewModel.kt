package com.example.smartexpensemanager.ui.auth.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.smartexpensemanager.data.repository.AuthRepository
import com.example.smartexpensemanager.data.repository.AuthRepositoryImpl
import com.example.smartexpensemanager.ui.auth.login.IdentifierError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {
    private val authRepository: AuthRepository = AuthRepositoryImpl()
    private val _uiState = MutableStateFlow(
        RegisterUiState(
            formState = RegisterFormState(
                identifier = savedState[KEY_IDENTIFIER] ?: ""
            )
        )
    )
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                formState = state.formState.copy(name = value)
            )
        }
    }

    fun onIdentifierChanged(value: String) {
        savedState[KEY_IDENTIFIER] = value
        _uiState.update { state ->
            state.copy(
                formState = state.formState.copy(
                    identifier = value,
                    identifierError = null
                ),
                submitStatus = SubmitStatus.Idle
            )
        }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                formState = state.formState.copy(phone = value)
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                formState = state.formState.copy(
                    password = value,
                    passwordError = null
                ),
                submitStatus = SubmitStatus.Idle
            )

        }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                formState = state.formState.copy(
                    confirmPassword = value,
                    confirmPasswordError = null
                ),
                submitStatus = SubmitStatus.Idle
            )
        }
    }

    fun onAgreeTermsChanged(checked: Boolean) {
        _uiState.update { state ->
            state.copy(
                formState = state.formState.copy(
                    agreeTerms = checked,
                    termsRequiredError = false
                )
            )
        }
    }

    fun register() {
        val form = _uiState.value.formState
        val validation = validateForm(form)


    }

    private fun validatePassword(value: String): PasswordError? {
        val passwordError = when {
            value.isEmpty() -> PasswordError.REQUIRED
            !value.any { it.isDigit() } -> PasswordError.MISSING_DIGIT
            !value.any { it.isUpperCase() } -> PasswordError.MISSING_UPPERCASE
            !value.any { it.isLowerCase() } -> PasswordError.MISSING_LOWERCASE
            !value.any {
                !it.isLetterOrDigit()
                        && !it.isWhitespace()
            } -> PasswordError.MISSING_SPECIAL_CHAR

            value.length < 8 -> PasswordError.TOO_SHORT
            else -> null
        }
        return passwordError
    }

    private data class ValidationResult(
        val identifierError: IdentifierError?,
        val passwordError: PasswordError?,
        val confirmPasswordError: ConfirmPasswordError?,
        val termsRequiredError: Boolean
    ) {
        val isComplete: Boolean
            get() = identifierError == null &&
                    passwordError == null &&
                    confirmPasswordError == null &&
                    !termsRequiredError
    }

    private fun validateForm(form: RegisterFormState): ValidationResult {
        val idErr = validateIdentifier(form.identifier)
        val passErr = validatePassword(form.password)
        val confirmPasswordError = when{
            form.confirmPassword.isEmpty() -> ConfirmPasswordError.REQUIRED
            form.confirmPassword != form.password -> ConfirmPasswordError.MISMATCH
            else -> null
        }
        return ValidationResult(
            identifierError = idErr,
            passwordError = passErr,
            confirmPasswordError = confirmPasswordError,
            termsRequiredError = !form.agreeTerms
        )
    }

    private fun validateIdentifier(value: String): IdentifierError? {
        val id = value.trim()
        if (id.isEmpty()) return IdentifierError.REQUIRED
        val isEmail = EMAIL_REGEX.matches(id)
        val isPhone = PHONE_REGEX.matches(
            id.replace(PHONE_SEPARATORS, ""))
        return if (isEmail || isPhone) null else IdentifierError.INVALID
    }

    companion object {
        private const val KEY_IDENTIFIER = "register_identifier"
        private val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        private val PHONE_REGEX = Regex("^\\+?[0-9]{8,15}$")
        private val PHONE_SEPARATORS = Regex("[\\`s().-]")
    }
}