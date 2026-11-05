package com.example.smartexpensemanager.ui.auth.login

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        // Khoi tao bat su kien
        setupListener()
        // Khoi tao bo lang nghe tu viewmodel
        observeState()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListener() = with(binding) {

        // Nut quay lai
        loginBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Bat su kien user go email / sdt -> Truyen vau ViewModel
        loginIdentifier.doAfterTextChanged {
            viewModel.onIdentifierChanged(it?.toString().orEmpty())
        }

        // Bat su kien user go password -> Truyen vao ViewModel
        loginPassword.doAfterTextChanged {
            viewModel.onPasswordChanged(it?.toString().orEmpty())
        }

        // Bat su kien checkbox
        loginRemember.setOnCheckedChangeListener { _, checked ->
            viewModel.onRememberSessionChanged(checked)
        }

        // Bat/tat hien password
        loginPasswordToggle.setOnClickListener {
            val show = loginPassword.transformationMethod is PasswordTransformationMethod
            loginPassword.transformationMethod = if (show) null else PasswordTransformationMethod.getInstance()
            loginPassword.setSelection(loginPassword.text?.length ?: 0)
        }

        // Nut login
        loginSubmit.setOnClickListener {
            viewModel.login()
        }

        // Cac nut chuc nang tam thoi
        loginForgot.setOnClickListener {
            showUnavailable(R.string.login_forgot_password, R.string.login_forgot_unavailable)
        }
        loginSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }
        loginGoogle.setOnClickListener {
            showUnavailable(R.string.login_google, R.string.login_google_unavailable)
        }
        loginApple.setOnClickListener {
            showUnavailable(R.string.login_apple, R.string.login_apple_unavailable)
        }
        loginBiometric.setOnClickListener {
            showUnavailable(R.string.login_biometric, R.string.login_biometric_unavailable)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderUi(state)
                }
            }
        }

    }

    private fun renderUi(state: LoginUiState) = with(binding) {
        // Cap nhat text email neu khac data hien tai
        if (loginIdentifier.text?.toString() != state.identifier) {
            loginIdentifier.setText(state.identifier)
        }

        // Cap nhat password neu khac data hien tai
        if (loginPassword.text?.toString() != state.password) {
            loginPassword.setText(state.password)
        }

        // Cap nhat checkbox ghi nho
        if (loginRemember.isChecked != state.rememberSession) {
            loginRemember.isChecked = state.rememberSession
        }

        // Cap nhat loi o nhap email
        loginIdentifierError.isVisible = state.identifierError != null
        state.identifierError?.let {
            loginIdentifierError.setText(
                if (it == IdentifierError.REQUIRED) R.string.login_identifier_required
                else R.string.login_identifier_invalid
            )
        }

        if(state.isSuccess){
            findNavController().navigate(R.id.action_login_to_dashboard)
            return@with
        }

        state.errorMessage?.let{ _->
            showUnavailable(R.string.login_header,R.string.login_failed )
        }

        // Cap nhat loi password
        loginPasswordError.isVisible = state.passwordRequired

        // Cap nhat trang thai thong bao dich vu chua san sang
        loginStatus.isVisible = state.authenticationUnavailable
    }

    // Hien thi dialog tam thoi
    private fun showUnavailable(titleRes: Int, messageRes: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(titleRes)
            .setMessage(messageRes)
            .setPositiveButton(R.string.login_close, null)
            .show()
    }
}
