package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.utils.FakerUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //DOEN: provide testing to the SaveReminderViewModel and its live data objects

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    val list = FakerUtils.generateRemindersDataItemList(1)
    private val reminder1 = list[0]

    private lateinit var fakeDataSource: FakeDataSource
    // Class under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel


    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun check_loading() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel =
                SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.validateAndSaveReminder(reminder1)

        val value = saveReminderViewModel.showLoading.getOrAwaitValue()

        Assert.assertThat(value, CoreMatchers.`is`(true))
    }

    @Test
    fun shouldReturnError() {
        fakeDataSource = FakeDataSource(null)
        saveReminderViewModel =
                SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)

        reminder1.title = null
        saveReminderViewModel.validateAndSaveReminder(reminder1)

        val value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()

        Assert.assertThat(value, CoreMatchers.`is`(R.string.err_enter_title))
    }

}