package com.hollowlog.dailytracker.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hollowlog.dailytracker.database.dao.ActivityDao
import com.hollowlog.dailytracker.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActivityViewModel(
    private val dao: ActivityDao
) : ViewModel() {

    // Keeps track of the currently selected date
    private val _currentDate = MutableStateFlow(LocalDate.now())
    var currentDate: StateFlow<LocalDate> = _currentDate

    fun decreaseCurrentDate(numDays: Long) {
        _currentDate.value = currentDate.value.minusDays(numDays)
    }

    fun increaseCurrentDate(numDays: Long) {
        _currentDate.value = currentDate.value.plusDays(numDays)
    }

    fun setCurrentDate(newDate: LocalDate) {
        _currentDate.value = newDate
    }

    fun addActivity(activity: Activity) {
        viewModelScope.launch {
            dao.upsertActivity(activity)
        }
    }

    fun updateActivity(activity: Activity) {
        viewModelScope.launch {
            dao.upsertActivity(activity)
        }
    }

    suspend fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            dao.upsertActivity(activity)
        }
        dao.deleteActivity(activity)
    }

    suspend fun deleteActivityById(id: Int) {
        dao.deleteActivityById(id)
    }

    fun getAllActivities(): Flow<List<Activity>> {
        return dao.getAllActivities()
    }

    fun getAllActivitiesByDate(date: String): Flow<List<Activity>> {
        return dao.getAllActivitiesByDate(date)
    }

    fun getActivityById(id: Int): Flow<Activity> {
        return dao.getActivityById(id)
    }
}