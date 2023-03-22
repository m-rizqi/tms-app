package com.rizqi.tms.ui.onboarding

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rizqi.tms.R
import com.rizqi.tms.ui.espresso.EspressoTestsMatchers.Companion.withDrawable
import com.rizqi.tms.ui.login.LoginActivity
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class OnBoardingActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(OnBoardingActivity::class.java)

    @Before
    fun setUp(){
        Intents.init()
        activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @After
    fun tearDown(){
        Intents.release()
    }

    @Test
    fun test_swipe_event() {
        onView(withText(R.string.scan_and_save_item)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_save_see_items_in_your_merchant)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding1)))
        onView(withText(R.string.next)).check(matches(isDisplayed()))
        onView(withText(R.string.next)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))
        onView(withId(R.id.vp_onboarding)).perform(swipeLeft())

        onView(withText(R.string.easy_cashier_feature)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_to_speed_up_merchant_performance)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding2)))
        onView(withText(R.string.next)).check(matches(isDisplayed()))
        onView(withText(R.string.next)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))
        onView(withId(R.id.vp_onboarding)).perform(swipeLeft())

        onView(withText(R.string.see_financial_progress)).check(matches(isDisplayed()))
        onView(withText(R.string.income_resume_to_see_merchant_progress)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding3)))
        onView(withText(R.string.start)).check(matches(isDisplayed()))
        onView(withText(R.string.start)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))
        onView(withId(R.id.vp_onboarding)).perform(swipeRight())

        onView(withText(R.string.easy_cashier_feature)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_to_speed_up_merchant_performance)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding2)))
        onView(withText(R.string.next)).check(matches(isDisplayed()))
        onView(withText(R.string.next)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))
        onView(withId(R.id.vp_onboarding)).perform(swipeRight())

        onView(withText(R.string.scan_and_save_item)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_save_see_items_in_your_merchant)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding1)))
        onView(withText(R.string.next)).check(matches(isDisplayed()))
        onView(withText(R.string.next)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))

    }

    @Test
    fun test_click_next_button_event() {
        onView(withText(R.string.scan_and_save_item)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_save_see_items_in_your_merchant)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding1)))
        onView(withText(R.string.next)).check(matches(isDisplayed()))
        onView(withText(R.string.next)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))
        onView(withId(R.id.btn_onboarding_next)).perform(click())

        onView(withText(R.string.easy_cashier_feature)).check(matches(isDisplayed()))
        onView(withText(R.string.scan_to_speed_up_merchant_performance)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding2)))
        onView(withText(R.string.next)).check(matches(isDisplayed()))
        onView(withText(R.string.next)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))
        onView(withId(R.id.btn_onboarding_next)).perform(click())

        onView(withText(R.string.see_financial_progress)).check(matches(isDisplayed()))
        onView(withText(R.string.income_resume_to_see_merchant_progress)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.iv_onboarding), withDrawable(R.drawable.vector_onboarding3)))
        onView(withText(R.string.start)).check(matches(isDisplayed()))
        onView(withText(R.string.start)).check(matches(isClickable()))
        onView(withText(R.string.skip)).check(matches(isDisplayed()))
        onView(withText(R.string.skip)).check(matches(isClickable()))

    }

    @Test
    fun test_click_button_start_should_open_login_activity(){
        onView(withId(R.id.btn_onboarding_next)).perform(click())
        onView(withId(R.id.btn_onboarding_next)).perform(click())
        onView(withId(R.id.btn_onboarding_next)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun test_click_button_skip_should_open_login_activity(){
        onView(withId(R.id.tv_onboarding_skip)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun test_start_to_login_then_click_back_should_stay_in_on_boarding() {
        onView(withId(R.id.tv_onboarding_skip)).perform(click())
        Espresso.pressBack()
        Espresso.pressBack()
        onView(withId(R.id.btn_onboarding_next)).check(matches(isDisplayed()))
    }
}