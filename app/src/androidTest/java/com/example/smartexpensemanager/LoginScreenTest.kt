package com.example.smartexpensemanager

import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @Test fun loginFormSurvivesRecreationAndReturnsToOnboarding() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            onView(withId(R.id.onboarding_login)).perform(click())
            onView(withId(R.id.login_identifier)).perform(replaceText("user@example.com"))
            onView(withId(R.id.login_password)).perform(replaceText("private-password"), closeSoftKeyboard())
            onView(withId(R.id.login_password_toggle)).perform(click())
            scenario.onActivity {
                val password = it.findViewById<EditText>(R.id.login_password)
                assertNull(password.transformationMethod)
                assertFalse(password.isSaveEnabled)
            }
            scenario.recreate()
            onView(withId(R.id.login_identifier)).check(matches(withText("user@example.com")))
            onView(withId(R.id.login_password)).check(matches(withText("private-password")))
            scenario.onActivity {
                assertTrue(it.findViewById<EditText>(R.id.login_password).transformationMethod is PasswordTransformationMethod)
            }
            pressBack()
            onView(withId(R.id.onboarding_start)).check(matches(isDisplayed()))
        }
    }

    @Test fun emptyCredentialsShowInlineErrors() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.onboarding_login)).perform(click())
            onView(withId(R.id.login_submit)).perform(scrollTo(), click())
            onView(withId(R.id.login_identifier_error))
                .check(matches(withText(R.string.login_identifier_required)))
            onView(withId(R.id.login_password_error))
                .check(matches(withText(R.string.login_password_required)))
        }
    }
}
