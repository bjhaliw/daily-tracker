package com.hollowlog.dailytracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * An Activity is any loggable action within the application.
 *
 * The user creates a name for the activity (i.e. the subject) and the date is automatically assigned to it.
 * The user can then add optional comments to the Activity if they wish.
 */
@Entity(tableName = "activities")
data class Activity(
    val name: String,

    val date: LocalDate,

    val comments: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)