package com.hollowlog.dailytracker.database

import android.content.Context
import androidx.room.Room

object ActivityDatabaseProvider {

    @Volatile
    private var INSTANCE: ActivityDatabase? = null

    fun getDatabase(context: Context): ActivityDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context = context.applicationContext,
                klass = ActivityDatabase::class.java,
                name = "activities.db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}