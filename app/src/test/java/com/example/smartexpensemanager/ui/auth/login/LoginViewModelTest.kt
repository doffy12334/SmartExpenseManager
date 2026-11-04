package com.example.smartexpensemanager.ui.auth.login

import androidx.lifecycle.SavedStateHandle
import com.example.smartexpensemanager.ui.auth.login.IdentifierError
import com.example.smartexpensemanager.ui.auth.login.LoginViewModel
import org.junit.Assert.*
import org.junit.Test

class LoginViewModelTest {
    @Test fun emptyFormReportsBothRequiredFields() {
        val model = LoginViewModel(SavedStateHandle())
        model.submit()
        assertEquals(IdentifierError.REQUIRED, model.uiState.value.identifierError)
        assertTrue(model.uiState.value.passwordRequired)
        assertFalse(model.uiState.value.authenticationUnavailable)
    }

    @Test fun invalidIdentifierIsRejectedAndClearsAfterCorrection() {
        val model = LoginViewModel(SavedStateHandle())
        model.onIdentifierChanged("not-an-email")
        model.onPasswordChanged("password")
        model.submit()
        assertEquals(IdentifierError.INVALID, model.uiState.value.identifierError)
        model.onIdentifierChanged("user@example.com")
        assertNull(model.uiState.value.identifierError)
    }

    @Test fun validPhoneDoesNotPretendAuthenticationSucceeded() {
        val model = LoginViewModel(SavedStateHandle())
        model.onIdentifierChanged("0912 345 678")
        model.onPasswordChanged(" p ")
        model.submit()
        assertNull(model.uiState.value.identifierError)
        assertFalse(model.uiState.value.passwordRequired)
        assertTrue(model.uiState.value.authenticationUnavailable)
        assertEquals(" p ", model.uiState.value.password)
    }

    @Test fun newViewModelRestoresNonSecretFieldsButNotPassword() {
        val savedState = SavedStateHandle()
        val original = LoginViewModel(savedState)
        original.onIdentifierChanged("user@example.com")
        original.onRememberSessionChanged(false)
        original.onPasswordChanged("private-password")
        val restored = LoginViewModel(savedState)
        assertEquals("user@example.com", restored.uiState.value.identifier)
        assertFalse(restored.uiState.value.rememberSession)
        assertEquals("", restored.uiState.value.password)
        assertEquals(setOf("login_identifier", "login_remember"), savedState.keys())
    }
}
