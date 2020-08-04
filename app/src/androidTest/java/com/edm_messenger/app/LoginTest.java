package com.edm_messenger.app;


import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.edm_messenger.R;

import junit.framework.AssertionFailedError;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void LoginTest() {
        //Check Email and Password EditView exists
        ViewInteraction emailEditView = onView(
                allOf(withId(R.id.login_email_edit_text), withHint("  Enter Email"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                2),
                        isDisplayed()));
        emailEditView.check(matches(withHint("  Enter Email")));
        ViewInteraction passwordEditView = onView(
                allOf(withId(R.id.login_password_edit_text), withHint("  Enter Password"),
                        isDisplayed()));
        passwordEditView.check(matches(withHint("  Enter Password")));
        SystemClock.sleep(2000);

        //Test Empty Email and Password
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1000);
        onView(withText("Please Enter Your Email and Password")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        SystemClock.sleep(1000);

        //Test Wrong Format Email and Empty Password
        onView(withId(R.id.login_email_edit_text)).perform(typeText("teemomustdie"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1000);
        onView(withText("Please Enter Your Password")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        SystemClock.sleep(1000);

        //Test Wrong Format Email
        onView(withId(R.id.login_password_edit_text)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1000);
        onView(withText("Please Enter Your Email in Correct Format")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        SystemClock.sleep(1000);

        //Test Not Existed Correct Format Email
        onView(withId(R.id.login_email_edit_text)).perform(clearText());
        onView(withId(R.id.login_email_edit_text)).perform(typeText("teemomustdie@teemo.mo"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(3000);
        onView(withText("The corresponding email could not be found")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        SystemClock.sleep(1000);

        //Test Correct Email Wrong Password
        onView(withId(R.id.login_email_edit_text)).perform(clearText());
        onView(withId(R.id.login_email_edit_text)).perform(typeText("unittester@ucsd.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_password_edit_text)).perform(clearText());
        onView(withId(R.id.login_password_edit_text)).perform(typeText("1234567"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        SystemClock.sleep(1000);
        onView(withText(startsWith("The password is invalid"))).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        SystemClock.sleep(1000);

        //Test Correct Email Correct Password
        onView(withId(R.id.login_password_edit_text)).perform(clearText());
        onView(withId(R.id.login_password_edit_text)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        ViewInteraction button = onView(
                allOf(withId(R.id.login_button),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                        0),
                                5),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        SystemClock.sleep(1000);
        onView(withId(R.id.login_button)).perform(click());

        SystemClock.sleep(3000);
        onView(withText("Login Successfully")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
        ViewInteraction settingIcon = onView(
                allOf(withId(R.id.settings_menu),
                        isDisplayed()));
        settingIcon.check(matches(isDisplayed()));
        SystemClock.sleep(3000);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}