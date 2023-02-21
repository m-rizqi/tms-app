package com.rizqi.tms.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingViewPagerAdapter(
    fragmentActivity : FragmentActivity,
    private val onBoardingContents : List<OnBoardingViewModel.OnBoardingContent>
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return onBoardingContents.size
    }

    override fun createFragment(position: Int): Fragment {
        return OnBoardingFragment(onBoardingContents[position])
    }
}