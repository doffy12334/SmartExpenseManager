package com.example.smartexpensemanager.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.data.remote.api.SupabaseProvider
import com.example.smartexpensemanager.data.remote.datasource.CategoryRemoteDataSource
import com.example.smartexpensemanager.data.remote.datasource.TransactionRemoteDataSource
import com.example.smartexpensemanager.databinding.FragmentDashBoardBinding
import com.google.android.material.snackbar.Snackbar
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var balanceVisible = true
    private lateinit var recentAdapter: RecentTransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBoardBinding.inflate(
            inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        balanceVisible = savedInstanceState?.getBoolean(
            KEY_BALANCE_VISIBLE, true) ?: true
        setupBalanceSection()
        setupRecentTransaction()
        setupListeners()
        testReadCategories()
        loadRecentTransactions()
    }


    private fun setupBalanceSection() {
        renderBalance()
        binding.dashboardBalance.dashboardBalanceVisibility.setOnClickListener {
            balanceVisible = !balanceVisible
            renderBalance()
        }
    }

    private fun renderBalance() {
        binding.dashboardBalance.dashboardBalance.setText(
            if (balanceVisible) R.string.dashboard_balance else R.string.dashboard_balance_hidden)
        binding.dashboardBalance.dashboardBalanceVisibility.contentDescription = getString(
            if (balanceVisible) R.string.dashboard_balance_visibility_action
            else R.string.dashboard_balance_show)
    }

    private fun setupRecentTransaction() {
        recentAdapter = RecentTransactionsAdapter { transaction ->
            Snackbar.make(
                binding.root,
                transaction.note ?: transaction.type,
                Snackbar.LENGTH_SHORT)
                .show()
        }
        binding.dashboardRecent.rvRecentTransaction.adapter = recentAdapter
    }

    private fun setupListeners() {
        binding.dashboardNavigation.dashboardNavHome.setOnClickListener {
            binding.dashboardScroll.smoothScrollTo(0, 0)
        }
        val pendingActions = listOf(
            binding.dashBoardHeader.dashboardNotification,
            binding.dashBoardHeader.dashboardProfile,
            binding.dashboardBalance.dashboardTransfer,
            binding.dashboardBalance.dashboardTopUp,
            binding.dashboardBalance.dashboardReport,
            binding.dashboardScan,
            binding.dashboardRecent.dashboardSeeAll,
            binding.dashboardNavigation.dashboardAdd,
            binding.dashboardNavigation.dashboardNavTransactions,
            binding.dashboardNavigation.dashboardNavBudget,
            binding.dashboardNavigation.dashboardNavSettings
        )
        pendingActions.forEach { actionView ->
            actionView.setOnClickListener {
                Snackbar.make(
                    binding.root,
                    R.string.dashboard_unavailable,
                    Snackbar.LENGTH_SHORT
                )
                    .setAnchorView(binding.dashboardNavigation.dashboardAdd)
                    .show()
            }
        }
    }

    private fun testReadCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val client = SupabaseProvider.supaClient
                val user = client.auth.currentUserOrNull()
                if (user == null){
                    Log.d("DashboardFragment", "User is not logged in")
                return@launch
            }
                val remote = CategoryRemoteDataSource()
                val categories = remote.getCategories(user.id)
                Log.d("DashboardFragment", "Categories: $categories")
            }catch (e: Exception) {
                Log.e("DashboardFragment", "Error reading categories", e)
            }
        }
    }

    private fun loadRecentTransactions() {
        viewLifecycleOwner.lifecycleScope.launch{
            try{
                val client = SupabaseProvider.supaClient
                val user = client.auth.currentUserOrNull() ?: return@launch
                val remote = TransactionRemoteDataSource()
                val transactions = remote.getTransactions(user.id)
                Log.d("DashboardFragment", "Recent transactions: $transactions")
                recentAdapter.submitList(transactions)
            }catch (e: Exception){
                Log.e("DashboardFragment", "Error loading recent transactions", e)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_BALANCE_VISIBLE,balanceVisible)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private companion object{
        const val KEY_BALANCE_VISIBLE = "dashboard.balanceVisible"
    }

}