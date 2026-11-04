package com.example.smartexpensemanager.ui.auth.login

/** Các loại lỗi kiểm tra định dạng tài khoản (Email / Số điện thoại) */
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
    val authenticationUnavailable: Boolean = false
)
