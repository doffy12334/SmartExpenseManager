package com.example.smartexpensemanager.ui.onboarding

import android.os.Bundle
import com.google.android.material.button.MaterialButton
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.smartexpensemanager.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Static onboarding UI; add a ViewModel when onboarding state is introduced. */
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.onboarding_start).apply {
            iconSize = (14 * resources.displayMetrics.density).toInt()
            setIconResource(R.drawable.onboarding_next)
            iconPadding = (8 * resources.displayMetrics.density).toInt()
            iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
            iconTint = null
        }
        fun pending(title: Int, message: Int = R.string.onboarding_pending) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title).setMessage(message)
                .setPositiveButton(R.string.onboarding_close, null).show()
        }
        view.findViewById<View>(R.id.onboarding_start).setOnClickListener { pending(R.string.onboarding_start) }
        view.findViewById<View>(R.id.onboarding_login).setOnClickListener { pending(R.string.onboarding_login) }
        view.findViewById<View>(R.id.onboarding_account).setOnClickListener { pending(R.string.onboarding_login) }
        view.findViewById<View>(R.id.onboarding_skip).setOnClickListener { pending(R.string.onboarding_start) }
        view.findViewById<View>(R.id.onboarding_back).setOnClickListener {
            pending(R.string.onboarding_back, R.string.onboarding_previous_pending)
        }
        val legal = view.findViewById<TextView>(R.id.onboarding_legal)
        val content = SpannableStringBuilder(HtmlCompat.fromHtml(getString(R.string.onboarding_legal), HtmlCompat.FROM_HTML_MODE_LEGACY))
        content.getSpans(0, content.length, URLSpan::class.java).forEach { link ->
            val start = content.getSpanStart(link)
            val end = content.getSpanEnd(link)
            content.removeSpan(link)
            content.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    pending(if (link.url == "terms") R.string.onboarding_terms else R.string.onboarding_policy, R.string.onboarding_legal_pending)
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        legal.text = content
        legal.movementMethod = LinkMovementMethod.getInstance()
    }
}
