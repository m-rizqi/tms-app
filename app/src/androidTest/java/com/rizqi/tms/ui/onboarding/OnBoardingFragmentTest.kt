package com.rizqi.tms.ui.onboarding

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rizqi.tms.R
import com.rizqi.tms.ui.espresso.EspressoTestsMatchers.Companion.withDrawable
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class OnBoardingFragmentTest {

    private var onBoardingViewModel = OnBoardingViewModel()
    private var onBoardingContents = onBoardingViewModel.uiState.value.onBoardingContents

    @Test
    fun test_on_boarding_fragment_1_content(){
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_TokoManagementSystem_NoActionBar) {
            OnBoardingFragment(onBoardingContents[0])
        }
        scenario.moveToState(Lifecycle.State.STARTED)
        onView(withText(R.string.scan_and_save_item)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_save_see_items_in_your_merchant)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_onboarding)).check(matches(withDrawable(R.drawable.vector_onboarding1)))
    }

    @Test
    fun test_on_boarding_fragment_2_content(){
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_TokoManagementSystem_NoActionBar) {
            OnBoardingFragment(onBoardingContents[1])
        }
        scenario.moveToState(Lifecycle.State.STARTED)
        onView(withText(R.string.easy_cashier_feature)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_to_speed_up_merchant_performance)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_onboarding)).check(matches(withDrawable(R.drawable.vector_onboarding2)))
    }

    @Test
    fun test_on_boarding_fragment_3_content(){
        val scenario = launchFragmentInContainer(themeResId = R.style.Theme_TokoManagementSystem_NoActionBar) {
            OnBoardingFragment(onBoardingContents[2])
        }
        scenario.moveToState(Lifecycle.State.STARTED)
        onView(withText(R.string.see_financial_progress)).check(matches(isDisplayed()))
        onView(withText(R.string.income_resume_to_see_merchant_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_onboarding)).check(matches(withDrawable(R.drawable.vector_onboarding3)))
    }
}