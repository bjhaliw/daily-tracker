package com.hollowlog.dailytracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hollowlog.dailytracker.database.dao.ActivityDao
import com.hollowlog.dailytracker.model.Activity

@Database(
    entities = [Activity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ActivityDatabase : RoomDatabase() {
    abstract val dao: ActivityDao
}