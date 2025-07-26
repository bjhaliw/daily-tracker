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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hollowlog.dailytracker.Routes
import com.hollowlog.dailytracker.model.Activity
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun DailyActivityScreen(
    navController: NavHostController,
    activityViewModel: ActivityViewModel
) {
    val selectedActivity by activityViewModel.selectedActivity.collectAsState()
    val currentDate by activityViewModel.currentDate.collectAsState()

    TrackerApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(navController, activityViewModel, selectedActivity)
            },
            bottomBar = {
                BottomAppBar {
                    Spacer(Modifier.weight(1f, true))

                    IconButton(
                        onClick = { activityViewModel.setCurrentDate(LocalDate.now()) },
                        enabled = currentDate != LocalDate.now()
                    ) {
                        Icon(Icons.Filled.Home, contentDescription = "Go to current date")
                    }
                    Spacer(Modifier.weight(1f, true))
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        onClick = { navController.navigate(Routes.CREATE_ACTIVITY_SCREEN + "/${false}") },
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.Add, "Create new activity")
                    }
                    Spacer(Modifier.weight(1f, true))
                    IconButton(onClick = { navController.navigate(Routes.CALENDAR_SCREEN) }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Go to calendar view")
                    }
                    Spacer(Modifier.weight(1f, true))
                }
            },
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
                        activityViewModel.getAllActivitiesForCurrentDate(currentDate.toString())
                            .collectAsState(initial = emptyList()).value.forEach { currActivity ->
                                Spacer(Modifier.height(20.dp))
                                Column(
                                    Modifier
                                        .padding(4.dp)
                                        .border(
                                            width = 2.dp,
                                            color = if (selectedActivity.id == currActivity.id) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.inversePrimary,
                                            shape = MaterialTheme.shapes.medium
                                        )
                                        .padding(4.dp)
                                        .clickable {

                                            if (selectedActivity.id == currActivity.id) {
                                                activityViewModel.resetSelectedActivity()
                                            } else {
                                                activityViewModel.setSelectedActivity(currActivity)
                                            }
                                        }
                                ) {
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(currActivity.name, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth()) {
                                        Text(currActivity.comments, fontStyle = FontStyle.Italic)
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
    activityViewModel: ActivityViewModel,
    selectedActivity: Activity
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Daily Activities") },
        actions = {
            if (selectedActivity != activityViewModel.EMPTY_ACTIVITY) {
                IconButton(
                    onClick = { isDialogOpen = true }
                ) {
                    Icon(Icons.Sharp.Delete, "Delete selected activity", tint = Color.Red)
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        navController.navigate(Routes.CREATE_ACTIVITY_SCREEN + "/true")
                    }
                ) {
                    Icon(Icons.Sharp.Edit, "Edit selected activity")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )

    if (isDialogOpen) {

        BasicAlertDialog(
            onDismissRequest = {
                isDialogOpen = false
            },

            ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Sharp.Delete, "Delete selected activity", tint = Color.Red)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Delete Activity", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        buildAnnotatedString {
                            append("Do you want to delete: ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(selectedActivity.name)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(Modifier.align(Alignment.End)) {
                        TextButton(
                            onClick = { isDialogOpen = false },
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                activityViewModel.deleteActivity(selectedActivity)
                                activityViewModel.resetSelectedActivity()
                                isDialogOpen = false
                            },
                        ) {
                            Text("Delete")
                        }
                    }

                }
            }
        }
    }
}

fun formatDateToString(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}