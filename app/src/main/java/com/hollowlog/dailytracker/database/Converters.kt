package com.hollowlog.dailytracker.database

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun toString(date: LocalDate?): String {
        if (date == null) {
            return ""
        }
        return date.toString()
    }

    @TypeConverter
    fun fromString(stringDate: String?): LocalDate? {
        if (stringDate.isNullOrBlank()) {
            return null
        }
        return LocalDate.parse(stringDate)
    }
}