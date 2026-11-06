package com.example.smartexpensemanager.ui.auth.recovery

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.FragmentRecoveryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecoveryFragment : Fragment(R.layout.fragment_recovery) {
    private var phoneSelected = true
    private var phoneValue = ""
    private var emailValue = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRecoveryBinding.bind(view)
        phoneSelected = savedInstanceState?.getBoolean("phoneSelected", true) ?: true
        phoneValue = savedInstanceState?.getString("phoneValue").orEmpty()
        emailValue = savedInstanceState?.getString("emailValue").orEmpty()

        binding.recoveryPhoneCard.setOnClickListener { selectMethod(binding, true) }
        binding.recoveryEmailCard.setOnClickListener { selectMethod(binding, false) }
        binding.recoverySendButton.setOnClickListener { submit(binding) }
        binding.recoveryHeaderBackButton.setOnClickListener { findNavController().navigateUp() }
        binding.recoveryHelpButton.setOnClickListener {
            unavailable(R.string.recovery_help, R.string.recovery_help_unavailable)
        }
        renderMethod(binding)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        view?.let { saveInput(FragmentRecoveryBinding.bind(it)) }
        outState.putBoolean("phoneSelected", phoneSelected)
        outState.putString("phoneValue", phoneValue)
        outState.putString("emailValue", emailValue)
    }

    private fun selectMethod(binding: FragmentRecoveryBinding, usePhone: Boolean) {
        if (phoneSelected == usePhone) return
        saveInput(binding)
        phoneSelected = usePhone
        renderMethod(binding)
    }

    private fun saveInput(binding: FragmentRecoveryBinding) {
        val value = binding.recoveryIdentifier.text.toString()
        if (phoneSelected) phoneValue = value else emailValue = value
    }

    private fun renderMethod(binding: FragmentRecoveryBinding) = with(binding) {
        renderCard(recoveryPhoneCard, recoveryPhoneIndicator, recoveryPhoneCheck, phoneSelected)
        renderCard(recoveryEmailCard, recoveryEmailIndicator, recoveryEmailCheck, !phoneSelected)
        recoveryPrefix.isVisible = phoneSelected
        recoveryFieldLabel.setText(
            if (phoneSelected) R.string.recovery_registered_phone else R.string.recovery_registered_email
        )
        recoveryIdentifier.inputType = if (phoneSelected) InputType.TYPE_CLASS_PHONE
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            recoveryIdentifier.setAutofillHints(
                if (phoneSelected) View.AUTOFILL_HINT_PHONE else View.AUTOFILL_HINT_EMAIL_ADDRESS
            )
        }
        recoveryIdentifier.setHint(if (phoneSelected) R.string.recovery_phone_hint else R.string.recovery_email_hint)
        recoveryIdentifier.setText(if (phoneSelected) phoneValue else emailValue)
        recoveryIdentifier.error = null
        val actionText = if (phoneSelected) R.string.recovery_send else R.string.recovery_send_link
        recoverySendLabel.setText(actionText)
        recoverySendButton.contentDescription = getString(actionText)
    }

    private fun renderCard(card: View, indicator: View, check: View, selected: Boolean) {
        card.isSelected = selected
        card.alpha = if (selected) 1f else .8f
        val tint = if (selected) R.color.finora_teal else R.color.finora_divider
        indicator.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), tint))
        check.isVisible = selected
    }

    private fun submit(binding: FragmentRecoveryBinding) {
        val input = binding.recoveryIdentifier
        val value = input.text.toString().trim()
        val valid = if (phoneSelected) {
            value.all { it.isDigit() || it in "+ -()" } && value.count(Char::isDigit) in 9..12
        } else Patterns.EMAIL_ADDRESS.matcher(value).matches()
        when {
            value.isEmpty() -> input.error = getString(R.string.login_identifier_required)
            !valid -> input.error = getString(R.string.login_identifier_invalid)
            else -> unavailable(R.string.login_forgot_password, R.string.login_forgot_unavailable)
        }
    }

    private fun unavailable(title: Int, message: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.login_close, null)
            .show()
    }
}

