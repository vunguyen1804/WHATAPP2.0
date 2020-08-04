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

import static androidx.test.espresso.action.ViewActions.clearText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class forgetPasswordTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void forgetPasswordTest() {
        ViewInteraction forgetPasswordLabel = onView(
                allOf(withId(R.id.forget_password_label), withText("Forget Password?"),
                        childAtPosition(
                                allOf(withId(R.id.linear_layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                4)),
                                0),
                        isDisplayed()));
        forgetPasswordLabel.check(matches(isDisplayed()));
        onView(withId(R.id.forget_password_label)).perform(click());
        SystemClock.sleep(2000);
        ViewInteraction usernameEnter = onView(
                allOf(withId(R.id.email_edit_text), withHint("example@ucsd.edu"),
                        isDisplayed()));
        usernameEnter.check(matches(withHint("example@ucsd.edu")));

        ViewInteraction recoverSubmitBtn = onView(
                allOf(withId(R.id.submit_button),
                        isDisplayed()));
        recoverSubmitBtn.check(matches(isDisplayed()));
        onView(withId(R.id.submit_button)).perform(click());
        onView(withText("EMAIL IS EMPTY.")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        Random r = new Random();
        onView(withId(R.id.email_edit_text)).perform(typeText("dummy"+ r.nextInt(100001)+"ucsd.edu"));
        SystemClock.sleep(1000);
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.submit_button)).perform(click());
        onView(withText("EMAIL IS INVALID.")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));


        onView(withId(R.id.email_edit_text)).perform(clearText());
        onView(withId(R.id.email_edit_text)).perform(typeText("dummy"+ r.nextInt(100001)+"@ucsd.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.submit_button)).perform(click());
        onView(withText("Reset Instruction Has Been Sent")).inRoot(withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
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
