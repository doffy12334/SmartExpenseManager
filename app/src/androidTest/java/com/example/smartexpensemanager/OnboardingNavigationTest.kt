package com.example.smartexpensemanager

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingNavigationTest {
    @Test
    fun lastPageAndControlsSurviveActivityRecreation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.onboarding_skip)).perform(click())
            onView(withId(R.id.onboarding_start)).check(matches(withText(R.string.onboarding_start)))
            scenario.recreate()
            onView(withId(R.id.onboarding_start)).check(matches(withText(R.string.onboarding_start)))
            onView(withId(R.id.onboarding_indicators))
                .check(matches(withContentDescription("Hướng dẫn, bước 3 trên 3")))
            pressBack()
            onView(withId(R.id.onboarding_start)).check(matches(withText(R.string.onboarding_next)))
            onView(withId(R.id.onboarding_indicators))
                .check(matches(withContentDescription("Hướng dẫn, bước 2 trên 3")))
        }
    }

    @Test
    fun toolbarAndSystemBackReturnToPreviousPages() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.onboarding_start)).perform(click())
            onView(withId(R.id.onboarding_start)).perform(click())
            onView(withId(R.id.onboarding_back)).perform(click())
            onView(withId(R.id.onboarding_indicators))
                .check(matches(withContentDescription("Hướng dẫn, bước 2 trên 3")))
            pressBack()
            onView(withId(R.id.onboarding_indicators))
                .check(matches(withContentDescription("Hướng dẫn, bước 1 trên 3")))
            onView(withId(R.id.onboarding_back)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }
}
