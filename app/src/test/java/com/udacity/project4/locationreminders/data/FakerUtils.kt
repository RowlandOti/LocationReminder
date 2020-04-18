package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import io.bloco.faker.Faker

/**
 * Created by rowlandoti on 2020-04-18
 * Last modified 2020-04-18
 */
object FakerUtils {

    private val faker = Faker()

    fun generateRemindersDTOList(size: Int): List<ReminderDTO> {
        val list = arrayListOf<ReminderDTO>()

        for (i in 0 until size) {
            val title = faker.name.nameWithMiddle()
            val description = faker.company.catchPhrase()
            val location = faker.address.streetName()
            val latitude = (-360..360).random().toDouble()
            val longitude = (-360..360).random().toDouble()
            list.add(ReminderDTO(title, description, location, latitude, longitude))
        }

        return list
    }

    fun generateRemindersDataItemList(size: Int): List<ReminderDataItem> {
        val list = arrayListOf<ReminderDataItem>()

        for (i in 0 until size) {
            val title = faker.name.nameWithMiddle()
            val description = faker.company.catchPhrase()
            val location = faker.address.streetName()
            val latitude = (-360..360).random().toDouble()
            val longitude = (-360..360).random().toDouble()
            list.add(ReminderDataItem(title, description, location, latitude, longitude))
        }

        return list
    }
}