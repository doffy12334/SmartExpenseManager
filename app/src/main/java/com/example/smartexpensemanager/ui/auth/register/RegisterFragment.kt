package com.example.smartexpensemanager.ui.auth.register

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.FragmentRegisterBinding
import com.example.smartexpensemanager.ui.auth.login.IdentifierError
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        setupListener()
        observeState()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListener() = with(binding) {
        signupBack.setOnClickListener { findNavController().navigateUp() }
        signupName.doAfterTextChanged {
            it?.toString()
                .orEmpty()
        }
        signupIdentifier.doAfterTextChanged {
            it?.toString()
                .orEmpty()
        }
        signupPhone.doAfterTextChanged {
            it?.toString()
                .orEmpty()
        }
        signupPassword.doAfterTextChanged {
            it?.toString()
                .orEmpty()
        }
        signupConfirmPassword.doAfterTextChanged {
            it?.toString()
                .orEmpty()
        }
        signupTermsCheck.setOnCheckedChangeListener { _, checked ->
            viewModel.onAgreeTermsChanged(checked)
        }

        signupPasswordToggle.setOnClickListener {
            val show = signupPassword.transformationMethod is PasswordTransformationMethod
            signupPassword.transformationMethod = if (show) null
            else PasswordTransformationMethod.getInstance()
            signupPassword.setSelection(signupConfirmPassword.text?.length ?: 0)
        }

        signupSubmit.setOnClickListener {
            viewModel.register()
        }
        signupLoginButton.setOnClickListener {
            findNavController().navigateUp()
        }
        signupGoogle.setOnClickListener {
            Toast.makeText(context, R.string.signup_google, Toast.LENGTH_SHORT)
                .show()
        }

        signupApple.setOnClickListener {
            Toast.makeText(context, R.string.signup_apple, Toast.LENGTH_SHORT)
                .show()
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

    private fun renderUi(state: RegisterUiState)  = with(binding) {
        val form = state.formState

        signupName.setTextIfDifferent(form.name)
        signupIdentifier.setTextIfDifferent(form.identifier)
        signupPhone.setTextIfDifferent(form.phone)
        signupPassword.setTextIfDifferent(form.password)
        signupConfirmPassword.setTextIfDifferent(form.confirmPassword)

        if (signupTermsCheck.isChecked != form.agreeTerms) {
            signupTermsCheck.isChecked = form.agreeTerms
        }
        //Loi o nhap email
        signupIdentifierError.isVisible = form.identifierError != null
        form.identifierError?.let {
            signupIdentifierError.setText(
                if (it == IdentifierError.REQUIRED) R.string.signup_required
                else R.string.signup_invalid_email
            )
        }

        signupPasswordError.isVisible = form.passwordError != null
        form.passwordError?.let {
            signupPasswordError.setText(
                when(it){
                    PasswordError.REQUIRED ->  R.string.signup_required
                    PasswordError.TOO_SHORT -> R.string.signup_too_short
                    PasswordError.MISSING_DIGIT-> R.string.signup_missing_digit
                    PasswordError.MISSING_UPPERCASE-> R.string.signup_missing_uppercase
                    PasswordError.MISSING_LOWERCASE-> R.string.signup_missing_lowercase
                    PasswordError.MISSING_SPECIAL_CHAR-> R.string.signup_missing_special_character

                }
            )
        }

        signupConfirmError.isVisible = form.confirmPasswordError != null
        form.confirmPasswordError?.let {
            signupConfirmError.setText(
                when (it) {
                    ConfirmPasswordError.REQUIRED -> R.string.signup_required
                    ConfirmPasswordError.MISMATCH -> R.string.signup_password_mismatch
                }
            )
        }
        signupTermsError.isVisible = true
        when(val status = state.submitStatus){
            is SubmitStatus.Idle -> {
                signupSubmit.isEnabled = true
                signupErrorBanner.isVisible = false
            }
            is SubmitStatus.Loading -> {
                signupSubmit.isEnabled = false
                signupErrorBanner.isVisible = false
            }
            is SubmitStatus.Success ->{
                signupSubmit.isEnabled = true
                signupErrorBanner.isVisible = false
                Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
            }
            is SubmitStatus.Error -> {
                signupSubmit.isEnabled = true
                signupErrorBanner.isVisible = true
                signupErrorBanner.text = status.message
            }
        }

    }
    private fun TextView.setTextIfDifferent(newText: String) {
        if (text?.toString() != newText) setText(newText)
    }


}