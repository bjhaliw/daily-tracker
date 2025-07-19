package com.hollowlog.dailytracker.database

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun toString(date: LocalDate): String {
        return date.toString()
    }

    @TypeConverter
    fun fromString(stringDate: String): LocalDate {
        return LocalDate.parse(stringDate)
    }
}