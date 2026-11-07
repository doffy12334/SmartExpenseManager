package com.example.smartexpensemanager.ui.auth.register

import com.example.smartexpensemanager.ui.auth.login.IdentifierError

enum class PasswordError {
    REQUIRED, TOO_SHORT, MISSING_DIGIT, MISSING_UPPERCASE, MISSING_LOWERCASE,
    MISSING_SPECIAL_CHAR
}

enum class ConfirmPasswordError {
    REQUIRED, MISMATCH
}

data class RegisterFormState(
    val name: String = "",
    val identifier: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val agreeTerms: Boolean = false,
    val identifierError: IdentifierError? = null,
    val passwordError: PasswordError? = null,
    val confirmPasswordError: ConfirmPasswordError? = null,
    val termsRequiredError: Boolean = false
) {
    // A requirements indicator, not an estimate of password entropy.
    val passwordStrength: Int
        get() {
            if (password.isEmpty()) return 0
            val metRequirements = listOf(
                password.length >= 8,
                password.any { it.isDigit() },
                password.any { it.isUpperCase() },
                password.any { it.isLowerCase() },
                password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
            ).count { it }
            return when {
                metRequirements == 5 -> 3
                password.length >= 8 && metRequirements >= 3 -> 2
                else -> 1
            }
        }

    val isComplete: Boolean
        get() = name.isNotBlank() &&
                identifier.isNotBlank() &&
                phone.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                agreeTerms
}

sealed interface SubmitStatus {
    data object Idle : SubmitStatus
    data object Loading : SubmitStatus
    data class Success(val message: String) : SubmitStatus
    data class Error(val message: String) : SubmitStatus
}

data class RegisterUiState(
    val formState: RegisterFormState = RegisterFormState(),
    val submitStatus: SubmitStatus = SubmitStatus.Idle
)









