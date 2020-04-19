package com.udacity.project4.utils

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.util.*

/**
 * Created by rowlandoti on 2020-04-19
 * Last modified $file.lastModified
 */

object TestDataFactory {

    fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    fun randomDouble(): Double {
        return Math.random()
    }

    fun generateLat(): Double {
        val min = -85.05
        val max = 85.05
        return min + (max - min) * randomDouble()
    }

    fun generateLong(): Double {
        val min = -180.0
        val max = 180.0
        return min + (max - min) * randomDouble()
    }

    fun generateEmail(): String {
        return "${randomString()}@xmail.com"
    }


    fun generateReminder(): ReminderDTO {
        return ReminderDTO(
                randomString(),
                randomString(),
                randomString(),
                generateLat(),
                generateLong(),
                randomString()
        )
    }
}