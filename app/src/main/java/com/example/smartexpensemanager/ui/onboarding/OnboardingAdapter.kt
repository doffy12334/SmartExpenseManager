package com.example.smartexpensemanager.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.smartexpensemanager.ui.onboarding.fragment.CashflowOnboardingFragment
import com.example.smartexpensemanager.ui.onboarding.fragment.ReceiptOnboardingFragment
import com.example.smartexpensemanager.ui.onboarding.fragment.SecurityOnboardingFragment

//Truyền Fragment cha vào FragmentStateAdapter giúp adapter quản lý
// các trang bằng child FragmentManager và lifecycle của Fragment cha.
class OnboardingAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = PAGE_COUNT


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CashflowOnboardingFragment()
            1 -> ReceiptOnboardingFragment()
            2 -> SecurityOnboardingFragment()
            else -> error("Trang không hợp lệ")
        }
    }

    companion object {
        const val PAGE_COUNT = 3
    }

}
