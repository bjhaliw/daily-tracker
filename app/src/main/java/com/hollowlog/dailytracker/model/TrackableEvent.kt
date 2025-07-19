package com.hollowlog.dailytracker.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "trackable_events")
data class TrackableEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val color: Color
)