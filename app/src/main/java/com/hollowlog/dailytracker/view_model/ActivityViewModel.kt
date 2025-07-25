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

    val EMPTY_ACTIVITY = Activity("Empty", LocalDate.now(), LocalDate.now(), "Empty", -1)

    // Keeps track of the currently selected date
    private val _currentDate = MutableStateFlow(LocalDate.now())
    var currentDate: StateFlow<LocalDate> = _currentDate

    // Keeps track of the currently selected Activity
    private val _selectedActivity = MutableStateFlow(EMPTY_ACTIVITY)
    var selectedActivity: StateFlow<Activity> = _selectedActivity

    ////////////////////////////
    // CURRENT DATE FUNCTIONS //
    ////////////////////////////
    fun decreaseCurrentDate(numDays: Long) {
        _currentDate.value = currentDate.value.minusDays(numDays)
        resetSelectedActivity()
    }

    fun increaseCurrentDate(numDays: Long) {
        _currentDate.value = currentDate.value.plusDays(numDays)
        resetSelectedActivity()
    }

    fun setCurrentDate(newDate: LocalDate) {
        _currentDate.value = newDate
        resetSelectedActivity()
    }

    ///////////////////////////////////
    //  SELECTED ACTIVITY FUNCTIONS  //
    ///////////////////////////////////
    fun setSelectedActivity(activity: Activity) {
        _selectedActivity.value = activity
    }

    fun resetSelectedActivity() {
        _selectedActivity.value = EMPTY_ACTIVITY
    }

    ////////////////////////////
    //  REPOSITORY FUNCTIONS  //
    ////////////////////////////
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

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch {
            dao.deleteActivity(activity)
        }
    }

    suspend fun deleteActivityById(id: Int) {
        dao.deleteActivityById(id)
    }

    fun getAllActivities(): Flow<List<Activity>> {
        return dao.getAllActivities()
    }

    fun getAllActivitiesByStartDate(date: String): Flow<List<Activity>> {
        return dao.getAllActivitiesByStartDate(date)
    }

    fun getActivityById(id: Int): Flow<Activity> {
        return dao.getActivityById(id)
    }

    fun getAllActivitiesBetweenDates(startDate: String, endDate: String): Flow<List<Activity>> {
        return dao.getAllActivitiesBetweenStartAndEndDates(startDate, endDate)
    }

    fun getAllActivitiesForCurrentDate(date: String): Flow<List<Activity>> {
        return dao.getAllActivitiesForCurrentDate(date)
    }

}