package com.example.mob_dev_portfolio

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import org.hamcrest.CoreMatchers.containsString


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testUIInitialization() {
        // Check if the RecyclerViews are initialized and visible
        onView(withId(R.id.offensiveRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.defensiveRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.pastGamesRecyclerView)).check(matches(isDisplayed()))

        // Check if the default season spinner is set to "2023"
        onView(withId(R.id.seasonSpinner)).check(matches(withSpinnerText(containsString("2023"))))
    }

    @Test
    fun testRecyclerViewScroll() {
        // Assuming RecyclerView's item ID is 'offensiveRecyclerView' for testing scroll
        onView(withId(R.id.offensiveRecyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10)) // Scroll to 10th item
    }

    @Test
    fun testClickOnRecyclerViewItem() {
        // Click on the 5th item in the offensive RecyclerView
        onView(withId(R.id.offensiveRecyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(4, click())) // Index starts from 0
    }
}
