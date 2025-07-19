package com.hollowlog.dailytracker.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.sharp.DateRange
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hollowlog.dailytracker.Routes
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DailyActivityScreen(
    navController: NavHostController,
    activityViewModel: ActivityViewModel
) {
    var selectedActivity by remember { mutableIntStateOf(0) }
    val NOT_SELECTED = -1

    val currentDate by activityViewModel.currentDate.collectAsState()

    TrackerApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(navController, selectedActivity, activityViewModel, onDeselect = { selectedActivity = 0 })
            },
            bottomBar = {BottomAppBar(
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Routes.CALENDAR_SCREEN) }
                    ) {
                        Icon(Icons.Sharp.DateRange, "Go to Calendar View")
                    }
                    IconButton(
                        onClick = { activityViewModel.setCurrentDate(LocalDate.now()) }
                    ) {
                        Icon(Icons.Sharp.Home, "Return to today's date")
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { navController.navigate(Routes.CREATE_ACTIVITY_SCREEN + "/$currentDate") }) {
                        Icon(Icons.Filled.Add, "Add a new activity")
                    }
                }
            ) },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .then(Modifier.fillMaxWidth())
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            IconButton(onClick = {
                                activityViewModel.decreaseCurrentDate(1)
                                selectedActivity = 0
                            }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Select Previous Day")
                            }

                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(currentDate.dayOfWeek.name)
                                Text(formatDateToString(currentDate))
                            }

                            IconButton(onClick = {
                                activityViewModel.increaseCurrentDate(1)
                                selectedActivity = 0
                            }) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Select Next Day")
                            }
                        }
                    }

                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false),
                    ) {
                        activityViewModel.getAllActivitiesByDate(currentDate.toString())
                            .collectAsState(initial = emptyList()).value.forEach { activity ->
                                Spacer(Modifier.height(20.dp))
                                Column(
                                    Modifier
                                        .padding(4.dp)
                                        .border(
                                            width = 2.dp,
                                            color = if (selectedActivity == activity.id) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.inversePrimary,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .padding(4.dp)
                                        .clickable {

                                            selectedActivity =
                                                if (selectedActivity == activity.id || selectedActivity == NOT_SELECTED) {
                                                    0
                                                } else {
                                                    activity.id
                                                }

                                        }
                                ) {
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(activity.name, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(activity.comments, fontStyle = FontStyle.Italic)
                                    }
                                }
                            }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    selectedActivity: Int,
    activityViewModel: ActivityViewModel,
    onDeselect: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = { Text("Daily Activities") },
        actions = {
            if (selectedActivity > 0) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            activityViewModel.deleteActivityById(selectedActivity)
                            onDeselect()
                        }
                    }
                ) {
                    Icon(Icons.Sharp.Delete, "Delete selected activity", tint = Color.Red)
                }
                IconButton(
                    onClick = { navController.navigate(Routes.CALENDAR_SCREEN) }
                ) {
                    Icon(Icons.Sharp.Edit, "Edit selected activity")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}


fun formatDateToString(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}