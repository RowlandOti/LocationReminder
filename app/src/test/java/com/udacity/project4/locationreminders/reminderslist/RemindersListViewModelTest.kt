package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.FakerUtils
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //DONE: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    val list = FakerUtils.generateRemindersDTOList(3)
    private val reminder1 = list[0]
    private val reminder2 = list[1]
    private val reminder3 = list[2]

    private lateinit var fakeDataSource: FakeDataSource
    // Class under test
    private lateinit var reminderListViewModel: RemindersListViewModel

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getRemindersList_and_reminderList_not_empty() {
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel =
                RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        reminderListViewModel.loadReminders()

        val value = reminderListViewModel.remindersList.getOrAwaitValue()

        assertThat(value, (not(emptyList())))
        assertThat(value.size, `is`(remindersList.size))
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource(mutableListOf())
        reminderListViewModel =
                RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        mainCoroutineRule.pauseDispatcher()

        reminderListViewModel.loadReminders()

        val value = reminderListViewModel.showLoading.getOrAwaitValue()

        assertThat(value, `is`(true))
    }

    @Test
    fun shouldReturnError() {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel =
                RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        reminderListViewModel.loadReminders()

        val value = reminderListViewModel.showSnackBar.getOrAwaitValue()

        assertThat(value, `is`("No reminders found"))
    }
}