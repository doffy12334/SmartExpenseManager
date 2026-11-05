package com.example.smartexpensemanager.ui.auth.signup

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.FragmentSignupBinding
import com.google.android.material.snackbar.Snackbar

class SignupFragment : Fragment(R.layout.fragment_signup) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSignupBinding.bind(view)
        binding.signupBack.setOnClickListener { findNavController().navigateUp() }
        binding.signupLogin.setOnClickListener { findNavController().navigateUp() }
        binding.signupCountry.setOnClickListener {
            showMessage(binding, R.string.signup_country_vietnam)
        }
        binding.signupShowPassword.togglePassword(binding.signupPassword, R.string.signup_show_password_desc)
        binding.signupShowConfirm.togglePassword(binding.signupConfirm, R.string.signup_show_confirm_desc)
        binding.signupPassword.doAfterTextChanged {
            updateStrength(binding, it?.toString().orEmpty())
        }
        binding.signupSubmit.setOnClickListener { submit(binding) }
        binding.signupConfirm.setOnEditorActionListener { _, action, _ ->
            if (action != EditorInfo.IME_ACTION_DONE) return@setOnEditorActionListener false
            submit(binding)
            true
        }
        binding.signupGoogle.setOnClickListener { showMessage(binding, R.string.login_google_unavailable) }
        binding.signupApple.setOnClickListener { showMessage(binding, R.string.login_apple_unavailable) }
    }

    private fun View.togglePassword(input: EditText, @StringRes showLabel: Int) {
        setOnClickListener {
            val show = input.transformationMethod is PasswordTransformationMethod
            val cursor = input.selectionEnd.coerceAtLeast(0)
            input.transformationMethod = if (show) null else PasswordTransformationMethod.getInstance()
            input.setSelection(cursor)
            contentDescription = getString(if (show) R.string.signup_hide_password else showLabel)
        }
    }

    private fun updateStrength(binding: FragmentSignupBinding, password: String) {
        val rules = passwordRules(password)
        val score = rules.count { it }
        binding.signupStrength.setText(
            when {
                password.isEmpty() -> R.string.signup_strength_empty
                score < 2 -> R.string.signup_strength_weak
                score == 2 -> R.string.signup_strength_medium
                else -> R.string.signup_strength_strong
            }
        )
        val active = ContextCompat.getColor(requireContext(), R.color.login_accent)
        val inactive = ContextCompat.getColor(requireContext(), R.color.signup_field)
        listOf(binding.signupBarOne, binding.signupBarTwo, binding.signupBarThree)
            .forEachIndexed { index, bar ->
                bar.backgroundTintList = ColorStateList.valueOf(if (index < score) active else inactive)
            }
        val muted = ContextCompat.getColor(requireContext(), R.color.login_hint)
        listOf(binding.signupRuleLength, binding.signupRuleUpper, binding.signupRuleNumber)
            .zip(listOf(binding.signupRuleLengthIcon, binding.signupRuleUpperIcon, binding.signupRuleNumberIcon))
            .forEachIndexed { index, (label, icon) ->
                val color = if (rules[index]) active else muted
                label.setTextColor(color)
                icon.imageTintList = ColorStateList.valueOf(color)
            }
    }

    private fun submit(binding: FragmentSignupBinding) {
        val email = binding.signupEmail.text.toString().trim()
        val password = binding.signupPassword.text.toString()
        when {
            binding.signupName.text.isBlank() -> binding.signupName.showError(R.string.signup_required)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                binding.signupEmail.showError(R.string.signup_invalid_email)
            !passwordRules(password).all { it } ->
                binding.signupPassword.showError(R.string.signup_invalid_password)
            password != binding.signupConfirm.text.toString() ->
                binding.signupConfirm.showError(R.string.signup_password_mismatch)
            !binding.signupTerms.isChecked -> showMessage(binding, R.string.signup_accept_terms)
            else -> showMessage(binding, R.string.signup_unavailable)
        }
    }

    private fun passwordRules(password: String): List<Boolean> =
        listOf(password.length >= 8, password.any { it in 'A'..'Z' }, password.any { it in '0'..'9' })

    private fun EditText.showError(@StringRes message: Int) {
        error = getString(message)
        requestFocus()
    }

    private fun showMessage(binding: FragmentSignupBinding, @StringRes message: Int) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
