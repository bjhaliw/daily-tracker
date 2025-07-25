package com.hollowlog.dailytracker.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hollowlog.dailytracker.model.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Upsert
    suspend fun upsertActivity(activity: Activity)

    @Delete
    suspend fun deleteActivity(activity: Activity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivityById(id: Int)

    @Query("SELECT * FROM activities ORDER BY name ASC")
    fun getAllActivities(): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    fun getActivityById(id: Int): Flow<Activity>

    @Query("SELECT * FROM activities WHERE startDate = :date")
    fun getAllActivitiesByStartDate(date: String): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE (:date BETWEEN startDate AND endDate) OR (startDate <= :date AND endDate IS \"\")")
    fun getAllActivitiesForCurrentDate(date: String): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE startDate BETWEEN :startDate AND :endDate")
    fun getAllActivitiesBetweenStartAndEndDates(startDate: String, endDate: String): Flow<List<Activity>>

}