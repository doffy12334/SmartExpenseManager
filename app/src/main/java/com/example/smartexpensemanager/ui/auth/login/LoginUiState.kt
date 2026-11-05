package com.example.smartexpensemanager.ui.auth.login

enum class IdentifierError { REQUIRED, INVALID }

/** Trạng thái toàn bộ giao diện Đăng nhập */
data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val rememberSession: Boolean = false,
    val submitted: Boolean = false,
    val isLoading: Boolean = false,
    val identifierError: IdentifierError? = null,
    val passwordRequired: Boolean = false,
    val authenticationUnavailable: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
