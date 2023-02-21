package com.rizqi.tms.ui.espresso

import android.view.View
import org.hamcrest.Matcher

class EspressoTestsMatchers {

    companion object {
        fun withDrawable(resourceId : Int) : Matcher<View> {
            return DrawableMatcher(resourceId)
        }
        fun noDrawable() : Matcher<View> {
            return DrawableMatcher(-1)
        }
    }
}