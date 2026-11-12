package com.example.smartexpensemanager.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.data.remote.api.SupabaseProvider
import com.example.smartexpensemanager.databinding.FragmentAuthGateBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

class AuthGateFragment : Fragment() {
    private var _binding: FragmentAuthGateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthGateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAuthGateBinding.bind(view)
        val auth = SupabaseProvider.supaClient.auth
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                auth.sessionStatus.collect {
                    when (it) {
                        SessionStatus.Initializing -> {
                            binding.authProgress.isVisible = true
                            binding.authStatus.isVisible = false
                        }

                        is SessionStatus.Authenticated ->
                            navigateFromGate(R.id.action_auth_gate_to_dashboard)

                        is SessionStatus.NotAuthenticated ->
                            navigateFromGate(R.id.action_auth_gate_to_login)

                        is SessionStatus.RefreshFailure -> {
                            binding.authProgress.isVisible = false
                            binding.authStatus.setText(R.string.auth_refresh_failed)
                            binding.authStatus.isVisible = true
                        }
                    }

                }
            }
        }
    }

    private fun navigateFromGate(actionId: Int) {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.authGateFragment) {
            navController.navigate(actionId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}