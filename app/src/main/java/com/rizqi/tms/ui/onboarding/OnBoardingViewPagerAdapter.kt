package com.rizqi.tms.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnBoardingViewPagerAdapter(
    fa : FragmentActivity
) : FragmentStateAdapter(fa){
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> OnBoarding1Fragment()
            1 -> OnBoarding2Fragment()
            else -> OnBoarding3Fragment()
        }
    }

}