package com.example.smartexpensemanager.ui.auth.register

import android.os.Bundle
import android.content.res.ColorStateList
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.content.ContextCompat
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
        setupListeners()
        observeState()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListeners() = with(binding) {
        signupBack.setOnClickListener { findNavController().navigateUp() }

        signupName.doAfterTextChanged {
            viewModel.onNameChanged(
                it?.toString().orEmpty())
        }
        signupIdentifier.doAfterTextChanged {
            viewModel.onIdentifierChanged(
                it?.toString().orEmpty())
        }
        signupPhone.doAfterTextChanged {
            viewModel.onPhoneChanged(
                it?.toString().orEmpty())
        }
        signupPassword.doAfterTextChanged {
            viewModel.onPasswordChanged(
                it?.toString().orEmpty())
        }
        signupConfirmPassword.doAfterTextChanged {
            viewModel.onConfirmPasswordChanged(
                it?.toString().orEmpty())
        }
        signupTermsCheck.setOnCheckedChangeListener { _, checked ->
            viewModel.onAgreeTermsChanged(checked)
        }

        signupPasswordToggle.setOnClickListener {
            val show = signupPassword.transformationMethod is PasswordTransformationMethod
            signupPassword.transformationMethod =
                if (show) null
                else PasswordTransformationMethod.getInstance()
            signupPassword.setSelection(signupPassword.text?.length ?: 0)
        }

        signupConfirmToggle.setOnClickListener {
            val show = signupConfirmPassword.transformationMethod is PasswordTransformationMethod
            signupConfirmPassword.transformationMethod =
                if (show) null
                else PasswordTransformationMethod.getInstance()

            signupConfirmPassword.setSelection(
                signupConfirmPassword.text?.length ?: 0
            )
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

                    val status = state.submitStatus
                    if (status is SubmitStatus.Success) {
                        Toast.makeText(
                            requireContext(),
                            status.message,
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.onSuccessHandled()
                        findNavController().navigate(R.id.action_signup_to_dashboard)
                    }
                }
            }
        }
    }

    private fun renderUi(state: RegisterUiState) {
        renderForm(state.formState)
        renderErrors(state.formState)
        renderSubmitStatus(state.submitStatus)
    }


    //Dong bo data len cac edt
    private fun renderForm(formState: RegisterFormState) = with(binding) {
        signupName.setTextIfDifferent(formState.name)
        signupIdentifier.setTextIfDifferent(formState.identifier)
        signupPhone.setTextIfDifferent(formState.phone)
        signupPassword.setTextIfDifferent(formState.password)
        signupConfirmPassword.setTextIfDifferent(formState.confirmPassword)
        renderPasswordStrength(formState.passwordStrength)
        if (signupTermsCheck.isChecked != formState.agreeTerms) {
            signupTermsCheck.isChecked = formState.agreeTerms
        }
    }

    private fun renderPasswordStrength(level: Int) = with(binding) {
        val label = when (level) {
            0 -> R.string.signup_strength_empty
            1 -> R.string.signup_strength_weak
            2 -> R.string.signup_strength_medium
            else -> R.string.signup_strength_strong
        }
        val color = ContextCompat.getColor(requireContext(), when (level) {
            1 -> R.color.finora_error
            2 -> R.color.finora_bills
            3 -> R.color.finora_emerald
            else -> R.color.finora_hint
        })
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.finora_divider)
        signupStrengthText.setText(label)
        signupStrengthText.setTextColor(color)
        listOf(strengthBar1, strengthBar2, strengthBar3).forEachIndexed { index, bar ->
            bar.backgroundTintList = ColorStateList.valueOf(
                if (index < level) color else inactiveColor
            )
        }
    }

    //Hien thi error
    private fun renderErrors(form: RegisterFormState) = with(binding) {
        signupIdentifierError.isVisible = form.identifierError != null

        form.identifierError?.let { error ->
            signupIdentifierError.setText(
                when (error) {
                    IdentifierError.REQUIRED -> R.string.signup_required
                    IdentifierError.INVALID -> R.string.signup_invalid_email
                }
            )
        }

        signupPasswordError.isVisible = form.passwordError != null

        form.passwordError?.let { error ->
            signupPasswordError.setText(
                when (error) {
                    PasswordError.REQUIRED -> R.string.signup_required

                    PasswordError.TOO_SHORT -> R.string.signup_too_short

                    PasswordError.MISSING_DIGIT -> R.string.signup_missing_digit

                    PasswordError.MISSING_UPPERCASE -> R.string.signup_missing_uppercase

                    PasswordError.MISSING_LOWERCASE -> R.string.signup_missing_lowercase

                    PasswordError.MISSING_SPECIAL_CHAR -> R.string.signup_missing_special_character
                }
            )
        }

        signupConfirmError.isVisible = form.confirmPasswordError != null

        form.confirmPasswordError?.let { error ->
            signupConfirmError.setText(
                when (error) {
                    ConfirmPasswordError.REQUIRED ->
                        R.string.signup_required

                    ConfirmPasswordError.MISMATCH ->
                        R.string.signup_password_mismatch
                }
            )
        }

        signupTermsError.isVisible = form.termsRequiredError
    }

    //Hien thi trang thai gui
    private fun renderSubmitStatus(status: SubmitStatus) = with(binding) {
        signupSubmit.isEnabled = status !is SubmitStatus.Loading
        signupErrorBanner.isVisible = status is SubmitStatus.Error

        if (status is SubmitStatus.Error) {
            signupErrorBanner.text = status.message
        }
    }

    private fun TextView.setTextIfDifferent(newText: String) {
        if (text?.toString() != newText) {
            text = newText
        }
    }
}
