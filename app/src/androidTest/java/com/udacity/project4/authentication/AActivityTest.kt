package com.udacity.project4.authentication


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.udacity.project4.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class AActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(AuthenticationActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION"
            )

    @Test
    fun aActivityTest() {
        val floatingActionButton = onView(
                allOf(
                        withId(R.id.addReminderFAB),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.refreshLayout),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton.perform(click())

        val imageButton = onView(
                allOf(
                        withId(R.id.saveReminder),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                4
                        ),
                        isDisplayed()
                )
        )
        imageButton.check(matches(isDisplayed()))

        val appCompatEditText = onView(
                allOf(
                        withId(R.id.reminderTitle),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText.perform(replaceText("range"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
                allOf(
                        withId(R.id.reminderDescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText2.perform(replaceText("rt"), closeSoftKeyboard())

        val appCompatTextView = onView(
                allOf(
                        withId(R.id.selectLocation), withText("Reminder Location"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
        appCompatTextView.perform(click())

        val button = onView(
                allOf(
                        withId(R.id.btn_done),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        button.check(matches(isDisplayed()))

        val appCompatButton = onView(
                allOf(
                        withId(R.id.btn_done), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatButton.perform(click())

        val floatingActionButton2 = onView(
                allOf(
                        withId(R.id.saveReminder),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0
                                ),
                                4
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton2.perform(click())

        onView(withText(R.string.geofences_added))
                .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.`is`(mActivityTestRule.activity.window.decorView))))
                .check(matches(isDisplayed()))

        val textView = onView(
                allOf(
                        withText("Lat: -1.26372, Long: 36.79458"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.reminderCardView),
                                        0
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
        textView.check(matches(withText("Lat: -1.26372, Long: 36.79458")))

        val textView2 = onView(
                allOf(
                        withText("Lat: -1.26372, Long: 36.79458"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.reminderCardView),
                                        0
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
        textView2.check(matches(withText("Lat: -1.26372, Long: 36.79458")))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
