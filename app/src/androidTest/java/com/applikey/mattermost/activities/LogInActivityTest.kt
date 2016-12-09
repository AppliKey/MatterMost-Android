package com.applikey.mattermost.activities

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.closeSoftKeyboard
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.applikey.mattermost.R
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogInActivityTest {

    val serverName = "http://mattermost-nutscracker53.herokuapp.com/"

    val userName = "allah@gmail.com"
    val password = "1111"


    @JvmField @get:Rule
    val activity: ActivityTestRule<ChooseServerActivity> = ActivityTestRule<ChooseServerActivity>(ChooseServerActivity::class.java)

    @Before
    fun before() {
        onView(withId(R.id.et_server)).perform(clearText())
    }

    @Test
    fun testThatCorrectServerAllowsToSignIn() {
        onView(withId(R.id.et_server)).perform(typeText(serverName))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).perform(click())
        onView(withId(R.id.et_login)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatProceedButtonIsDisabledWhenServerIsNotConfigure() {
        onView(withId(R.id.et_server)).perform(typeText(""))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).check(matches(not(isEnabled())))
    }

    @Test
    fun testThatIncorrectServerAddressShowsError() {
        onView(withId(R.id.et_server)).perform(typeText("sdgaqweaqwdasdf"))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).perform(click())
        onView(withId(R.id.et_server)).check(matches(hasErrorText(getStringRes(R.string.invalid_server_url))))
    }

    @Test
    fun testThatProceedButtonEnabledWhenServerFilled() {
        onView(withId(R.id.et_server)).perform(typeText("idud"))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatUserCanLoginAfterSuccessfullyEnteredServerAddress() {
        onView(withId(R.id.et_server)).perform(typeText(serverName))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).perform(click())
        onView(withId(R.id.et_login)).perform(typeText(userName))
        onView(withId(R.id.et_password)).perform(typeText(password))
        closeSoftKeyboard()
        onView(withId(R.id.b_authorize)).perform(click())
    }

    private fun getStringRes(res: Int): String {
        val targetContext = InstrumentationRegistry.getTargetContext()
        return targetContext.getString(res)
    }

}

