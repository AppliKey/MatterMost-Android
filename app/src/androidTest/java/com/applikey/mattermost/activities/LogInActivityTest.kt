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
import org.hamcrest.Matchers.any
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogInActivityTest {

    val serverName = "http://mattermost-nutscracker53.herokuapp.com/"

    val userName = "test5@gmail.com"
    val password = "11111"


    @JvmField @Rule
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
        onView(withId(R.id.rv_teams)).check(matches(isDisplayed()))
    }

    @Test
    fun testThatErrorWillShowIfPasswordNotEntered() {
        onView(withId(R.id.et_server)).perform(typeText(serverName))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).perform(click())
        onView(withId(R.id.et_login)).perform(typeText(userName))
        onView(withId(R.id.et_password)).perform(typeText(""))
        closeSoftKeyboard()
        onView(withId(R.id.b_authorize)).perform(click())
        onView(withId(R.id.et_password)).check(matches(hasErrorText(any(String::class.java))))
    }

    @Test
    fun testThatIncorrectLoginWillProduceNotFoundError() {
        onView(withId(R.id.et_server)).perform(typeText(serverName))
        closeSoftKeyboard()
        onView(withId(R.id.b_proceed)).perform(click())
        onView(withId(R.id.et_login)).perform(typeText("asdasd"))
        onView(withId(R.id.et_password)).perform(typeText("asdas"))
        closeSoftKeyboard()
        onView(withId(R.id.b_authorize)).perform(click())
        onView(withId(R.id.et_password)).check(matches(hasErrorText(any(String::class.java))))
    }

    private fun getStringRes(res: Int): String {
        val targetContext = InstrumentationRegistry.getTargetContext()
        return targetContext.getString(res)
    }

}

