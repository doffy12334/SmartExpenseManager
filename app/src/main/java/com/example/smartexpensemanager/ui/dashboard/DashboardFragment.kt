package com.example.smartexpensemanager.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.smartexpensemanager.R
import com.google.android.material.snackbar.Snackbar

class DashboardFragment : Fragment(R.layout.fragment_dash_board) {

    private var balanceVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        balanceVisible = savedInstanceState?.getBoolean(KEY_BALANCE_VISIBLE, true) ?: true

        val balance = view.findViewById<TextView>(R.id.dashboard_balance)
        val visibility = view.findViewById<View>(R.id.dashboard_balance_visibility)
        fun renderBalance() {
            balance.setText(if (balanceVisible) R.string.dashboard_balance else R.string.dashboard_balance_hidden)
            visibility.contentDescription = getString(
                if (balanceVisible) R.string.dashboard_balance_visibility_action else R.string.dashboard_balance_show
            )
        }
        renderBalance()
        visibility.setOnClickListener {
            balanceVisible = !balanceVisible
            renderBalance()
        }

        val scroll = view.findViewById<NestedScrollView>(R.id.dashboard_scroll)
        view.findViewById<View>(R.id.dashboard_nav_home).setOnClickListener {
            scroll.smoothScrollTo(0, 0)
        }
        val pendingActions = listOf(
            R.id.dashboard_notification,
            R.id.dashboard_profile,
            R.id.dashboard_transfer,
            R.id.dashboard_top_up,
            R.id.dashboard_report,
            R.id.dashboard_scan,
            R.id.dashboard_see_all,
            R.id.dashboard_add,
            R.id.dashboard_nav_transactions,
            R.id.dashboard_nav_budget,
            R.id.dashboard_nav_settings
        )
        pendingActions.forEach { id ->
            view.findViewById<View>(id).setOnClickListener {
                Snackbar.make(view, R.string.dashboard_unavailable, Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.dashboard_add)
                    .show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_BALANCE_VISIBLE, balanceVisible)
    }

    private companion object {
        const val KEY_BALANCE_VISIBLE = "dashboard.balanceVisible"
    }
}
