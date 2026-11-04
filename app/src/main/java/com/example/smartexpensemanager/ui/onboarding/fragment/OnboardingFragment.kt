package com.example.smartexpensemanager.ui.onboarding.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.smartexpensemanager.R
import com.example.smartexpensemanager.databinding.FragmentOnboardingBinding
import com.example.smartexpensemanager.ui.onboarding.OnboardingAdapter

/**
 * Fragment quản lý màn hình hướng dẫn ban đầu (Onboarding).
 * Bao gồm ViewPager2 chứa 3 slide hướng dẫn và thanh điều khiển chung (Back, Skip, Indicator, Next).
 */
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding: FragmentOnboardingBinding
        get() = requireNotNull(_binding)

    /** Callback can thiệp nút Back cử chỉ/hệ thống của thiết bị Android */
    private var btnCallback: OnBackPressedCallback? = null

    /** Callback lắng nghe sự kiện khi ViewPager2 chuyển trang */
    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            renderControls(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingBinding.bind(view)

        setupLegalText()
        setupBackNavigation()
        setupPager()
        setupListener()
    }

    private fun setupLegalText() {
        binding.onboardingLegal.text = HtmlCompat.fromHtml(
            getString(R.string.onboarding_legal),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    /** Khởi tạo ViewPager2 và gắn adapter nạp 3 trang slide */
    private fun setupPager() {
        binding.onboardingViewPager.apply {
            adapter = OnboardingAdapter(this@OnboardingFragment)
            registerOnPageChangeCallback(pageCallback)
        }
    }

    /** Đăng ký sự kiện Click cho các nút bấm (Next, Skip, Back) */
    private fun setupListener() {

        binding.onboardingStart.setOnClickListener {
            val pager = binding.onboardingViewPager
            val lastPosition = OnboardingAdapter.PAGE_COUNT - 1
            if (pager.currentItem < lastPosition) {
                pager.setCurrentItem(pager.currentItem + 1, true)
            } else {
                finishOnboarding()
            }
        }

        binding.onboardingSkip.setOnClickListener {
            val lastPosition = OnboardingAdapter.PAGE_COUNT - 1
            binding.onboardingViewPager.setCurrentItem(lastPosition, true)
        }

        binding.onboardingBack.setOnClickListener {
            previousPage()
        }
        binding.onboardingLogin.setOnClickListener { openLogin() }
        binding.onboardingAccount.setOnClickListener { openLogin() }
    }

    /** Lùi ViewPager2 về trang trước đó */
    private fun previousPage() {
        val pager = binding.onboardingViewPager
        if (pager.currentItem > 0) {
            pager.setCurrentItem(pager.currentItem - 1, true)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Khôi phục lại trạng thái giao diện đúng vị trí trang khi xoay màn hình
        renderControls(binding.onboardingViewPager.currentItem)
    }

    /** Hoàn tất Onboarding và chuyển sang màn hình tiếp theo */
    private fun finishOnboarding() {
        openLogin()
    }

    private fun openLogin() {
        val controller = findNavController()
        if (controller.currentDestination?.id == R.id.onboardingFragment) {
            controller.navigate(R.id.action_onboarding_to_login)
        }
    }

    /** Cấu hình nút Back cử chỉ/hệ thống của Android để lùi slide thay vì thoát app */
    private fun setupBackNavigation() {
        btnCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                previousPage()
            }
        }.also { callback ->
            requireActivity()
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, callback)
        }
    }

    /**
     * Cập nhật giao diện điều khiển theo vị trí trang hiện tại [position].
     * - Cập nhật trạng thái 3 chấm indicator (kích thước, màu sắc).
     * - Đổi text nút bấm ("Tiếp tục" vs "Bắt đầu ngay").
     * - Ẩn/Hiện nút Back và Skip.
     */
    private fun renderControls(position: Int) {
        val currentBinding = _binding ?: return
        val isLastPage = position == OnboardingAdapter.PAGE_COUNT - 1

        with(currentBinding) {
            onboardingIndicators.contentDescription = getString(
                R.string.onboarding_progress_format, position + 1, OnboardingAdapter.PAGE_COUNT
            )

            onboardingStart.setText(
                if (isLastPage) R.string.onboarding_start else R.string.onboarding_next
            )


            onboardingBack.isVisible = position > 0
            onboardingSkip.visibility = if (isLastPage) View.INVISIBLE else View.VISIBLE

            // Cập nhật trạng thái 3 chấm Indicator
            val indicators = listOf(indicatorDot1, indicatorDot2, indicatorDot3)
            indicators.forEachIndexed { index, indicator ->
                val isSelected = index == position
                indicator.layoutParams = indicator.layoutParams.apply {
                    width = resources.getDimensionPixelSize(
                        if (isSelected) {
                            R.dimen.onboarding_indicator_active_width
                        } else {
                            R.dimen.onboarding_indicator_inactive_width
                        }
                    )
                }
                indicator.setBackgroundResource(
                    if (isSelected) R.drawable.onboarding_primary else R.drawable.onboarding_dot
                )
            }
        }

        // Bật nút Back hệ thống khi ở trang 2 trở đi
        btnCallback?.isEnabled = position > 0
    }

    override fun onDestroyView() {
        // Giải phóng listener và adapter để tránh rò rỉ bộ nhớ
        _binding?.onboardingViewPager?.apply {
            unregisterOnPageChangeCallback(pageCallback)
            adapter = null
        }
        btnCallback?.remove()
        btnCallback = null
        _binding = null
        super.onDestroyView()
    }
}