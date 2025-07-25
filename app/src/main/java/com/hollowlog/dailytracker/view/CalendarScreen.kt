package com.hollowlog.dailytracker.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hollowlog.dailytracker.Routes
import com.hollowlog.dailytracker.core.convertMillisToDate
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navHostController: NavHostController, activityViewModel: ActivityViewModel) {
    val currentDate by activityViewModel.currentDate.collectAsState()
    val datePickerState = rememberDatePickerState(
//        initialSelectedDateMillis = currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    )
    val startDateString = convertMillisToDate(datePickerState.displayedMonthMillis)
    val startDateLocal = LocalDate.parse(startDateString)
    val endDateLocal = startDateLocal.withDayOfMonth(startDateLocal.month.length(startDateLocal.isLeapYear))
    val monthActivities = activityViewModel.getAllActivitiesBetweenDates(startDateString, endDateLocal.toString())
    val result by monthActivities.collectAsState(initial = emptyList())

    // Execute when the user selects a day from the Date Picker
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val stringDate = convertMillisToDate(millis)
            activityViewModel.setCurrentDate(LocalDate.parse(stringDate))
            navHostController.navigate(Routes.DAILY_ACTIVITY_SCREEN) {
                popUpTo(Routes.DAILY_ACTIVITY_SCREEN) {
                    inclusive = true
                }
            }
        }
    }

    TrackerApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(
                title = { Text("Calendar View") }, navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go to previous screen"
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }, content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        })
    }
}