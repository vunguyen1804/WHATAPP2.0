package com.edm_messenger.app;


import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.edm_messenger.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class createAccountTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createAccountTest() {
        ViewInteraction createAccountLabel = onView(
                allOf(withId(R.id.create_new_account_label), withText("Create a New Account?"),
                        childAtPosition(
                                allOf(withId(R.id.linear_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                4)),
                                1),
                        isDisplayed()));
        createAccountLabel.check(matches(withText("Create a New Account?")));

        onView(withId(R.id.create_new_account_label)).perform(click());

        SystemClock.sleep(2000);

        ViewInteraction usernameEnter = onView(
                allOf(withId(R.id.sign_up_username_edit_text), withHint("Enter Username"),
                        isDisplayed()));
        usernameEnter.check(matches(withHint("Enter Username")));
        Random r = new Random();
        onView(withId(R.id.sign_up_username_edit_text)).perform(typeText("test"+ r.nextInt(100001)));

        Espresso.closeSoftKeyboard();
        SystemClock.sleep(500);

        ViewInteraction emailEnter = onView(
                allOf(withId(R.id.sign_up_email_edit_text), withHint("example@ucsd.edu"),
                        isDisplayed()));
        emailEnter.check(matches(withHint("example@ucsd.edu")));
        onView(withId(R.id.sign_up_email_edit_text)).perform(typeText("test"+ r.nextInt(100001)+"@test.com"));

        Espresso.closeSoftKeyboard();
        SystemClock.sleep(500);

        ViewInteraction passwordEnter = onView(
                allOf(withId(R.id.sign_up_password_edit_text), withHint("Enter Password"),
                        isDisplayed()));
        passwordEnter.check(matches(withHint("Enter Password")));
        onView(withId(R.id.sign_up_password_edit_text)).perform(typeText("123456"));

        Espresso.closeSoftKeyboard();
        SystemClock.sleep(500);

        ViewInteraction button = onView(
                allOf(withId(R.id.signup_button),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        onView(withId(R.id.signup_button)).perform(click());

        SystemClock.sleep(5000);

        ViewInteraction emailEditView = onView(
                allOf(withId(R.id.login_email_edit_text), withHint("  Enter Email"),
                        isDisplayed()));
        emailEditView.check(matches(withHint("  Enter Email")));
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
