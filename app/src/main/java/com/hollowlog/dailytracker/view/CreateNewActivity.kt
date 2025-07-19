package com.hollowlog.dailytracker.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hollowlog.dailytracker.Routes
import com.hollowlog.dailytracker.model.Activity
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import java.time.LocalDate


@Composable
fun CreateNewActivityScreen(
    navController: NavHostController,
    localDate: LocalDate = LocalDate.now(),
    activityViewModel: ActivityViewModel,
) {
    TrackerApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { CreateActivityTopBar(navController, localDate) },
            content = { innerPadding ->
                CreateActivityContent(
                    modifier = Modifier.padding(innerPadding),
                    activityViewModel,
                    localDate,
                    navController
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityTopBar(navController: NavHostController, localDate: LocalDate) {
    TopAppBar(
        title = { Text("Create New Activity") },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(Routes.DAILY_ACTIVITY_SCREEN + "/${localDate}") {
                    popUpTo(Routes.CREATE_ACTIVITY_SCREEN + Routes.CREATE_ACTIVITY_SCREEN_ARGUMENTS) {
                        inclusive = true
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go to previous screen"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityContent(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    localDate: LocalDate,
    navController: NavHostController
) {
    var activityNameText by remember { mutableStateOf("") }
    var activityNameError by remember { mutableStateOf(false) }

    var commentsText by remember { mutableStateOf("") }
    var commentsError by remember { mutableStateOf(false) }

    var isChecked by remember { mutableStateOf(false) }

    var isDialogOpen by remember { mutableStateOf(false) }

    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = activityNameText,
            onValueChange = { input ->
                activityNameText = input
                activityNameError = input.isBlank()
            },
            label = { Text("Activity Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = activityNameError
        )

        OutlinedTextField(
            value = commentsText,
            onValueChange = { input ->
                commentsText = input
                commentsError = input.isBlank()
            },
            label = { Text("Comments") },
            modifier = Modifier.fillMaxWidth(),
            isError = commentsError
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
            Text("Make this activity trackable")
            IconButton(onClick = { isDialogOpen = true }) {
                Icon(Icons.Filled.Info, contentDescription = "Trackable Activity Help")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(
                onClick = {
                    activityViewModel.addActivity(Activity(activityNameText, localDate, commentsText))
                    navController.navigate(Routes.DAILY_ACTIVITY_SCREEN + "/${localDate}") {
                        popUpTo(Routes.CREATE_ACTIVITY_SCREEN + Routes.CREATE_ACTIVITY_SCREEN_ARGUMENTS) {
                            inclusive = true
                        }
                    }
                }) {
                Text("Save Activity")
            }
        }
    }

    if (isDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                isDialogOpen = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text =
                            "Trackable activities are events that you can later search and filter for. They " +
                                    "require a unique name to be registered in the application."
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = { isDialogOpen = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}
