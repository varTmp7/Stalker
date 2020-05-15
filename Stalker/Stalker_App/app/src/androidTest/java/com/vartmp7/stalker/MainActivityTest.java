/*
 * MIT License
 *
 * Copyright (c) 2020 VarTmp7
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.vartmp7.stalker;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void mainActivityTest() {
//        ViewInteraction appCompatButton = onView(
//                allOf(withId(R.id.btnProcediSenzaAuth), withText("Continua senza autenticazione"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.fcvLoginContainer),
//                                        0),
//                                2),
//                        isDisplayed()));
//        appCompatButton.perform(click());
//
//        ViewInteraction bottomNavigationItemView = onView(
//                allOf(withId(R.id.navigation_tracking), withContentDescription("Home"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(R.id.nav_view),
//                                        0),
//                                1),
//                        isDisplayed()));
//        bottomNavigationItemView.perform(click());
//
//        ViewInteraction appCompatButton2 = onView(
//                allOf(withId(R.id.btnStartAll), withText("Avvia\n tutti"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.LinearLayout")),
//                                        1),
//                                0),
//                        isDisplayed()));
//        appCompatButton2.perform(click());
//
//        ViewInteraction appCompatButton3 = onView(
//                allOf(withId(R.id.btnStartAll), withText("Avvia\n tutti"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.LinearLayout")),
//                                        1),
//                                0),
//                        isDisplayed()));
//        appCompatButton3.perform(click());
//
//        ViewInteraction appCompatButton4 = onView(
//                allOf(withId(R.id.btnStopAll), withText("Ferma \nTutti"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.LinearLayout")),
//                                        1),
//                                1),
//                        isDisplayed()));
//        appCompatButton4.perform(click());
//        onView(withId(R.id.btnStartAll)).check((view, noViewFoundException) -> isClickable());
//        ViewInteraction appCompatButton5 = onView(
//                allOf(withId(R.id.btnStartAll), withText("Avvia\n tutti"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.widget.LinearLayout")),
//                                        1),
//                                0),
//                        isDisplayed()));
//        appCompatButton5.perform(click());
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
