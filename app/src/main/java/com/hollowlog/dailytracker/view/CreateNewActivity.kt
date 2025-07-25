package com.hollowlog.dailytracker.view

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hollowlog.dailytracker.Routes
import com.hollowlog.dailytracker.model.Activity
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CreateAndEditActivityScreen(
    navController: NavHostController,
    activityViewModel: ActivityViewModel,
    isEdit: Boolean
) {
    TrackerApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { CreateActivityTopBar(navController) },
            content = { innerPadding ->
                CreateActivityContent(
                    modifier = Modifier.padding(innerPadding),
                    activityViewModel,
                    navController,
                    isEdit
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityTopBar(navController: NavHostController) {
    TopAppBar(
        title = { Text("Create New Activity") },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
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

@Composable
fun CreateActivityContent(
    modifier: Modifier,
    activityViewModel: ActivityViewModel,
    navController: NavHostController,
    isEdit: Boolean
) {
    val selectedActivity by activityViewModel.selectedActivity.collectAsState()
    val currDate by activityViewModel.currentDate.collectAsState()

    // Get the initial values to be used by the text fields
    val initialName = if (isEdit) selectedActivity.name else ""
    val initialComments = if (isEdit) selectedActivity.comments else ""

    // Keeps track of the values the user enters
    var activityNameText by remember { mutableStateOf(initialName) }
    var activityNameError by remember { mutableStateOf(false) }
    var commentsText by remember { mutableStateOf(initialComments) }
    var isChecked by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(currDate) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    // How much space should be between the components
    val SPACE_BETWEEN = Modifier.height(8.dp)

    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = activityNameText,
            onValueChange = { input ->
                activityNameText = input
                activityNameError = input.isBlank()
            },
            label = { Text("Activity Name *") },
            modifier = Modifier.fillMaxWidth(),
            isError = activityNameError
        )

        Spacer(SPACE_BETWEEN)

        OutlinedTextField(
            value = commentsText,
            onValueChange = { input -> commentsText = input },
            label = { Text("Comments") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(SPACE_BETWEEN)

        CreateAndEditActivityDateFields(
            getStartDate = { date -> startDate = date },
            getEndDate = { date -> endDate = date },
            showBoth = isChecked
        )

        Spacer(SPACE_BETWEEN)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
            Text(text = "This activity spans multiple days")
        }

        Spacer(SPACE_BETWEEN)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(
                onClick = {
                    val activity = Activity(
                        activityNameText,
                        startDate,
                        if (isChecked) endDate else startDate,
                        commentsText
                    )

                    if (isEdit) {
                        activity.id = activityViewModel.selectedActivity.value.id
                        activityViewModel.updateActivity(activity)
                    } else {
                        activityViewModel.addActivity(activity)
                    }

                    navController.navigate(Routes.DAILY_ACTIVITY_SCREEN) {
                        popUpTo(Routes.DAILY_ACTIVITY_SCREEN) {
                            inclusive = true
                        }
                    }
                }) {
                Text("Save Activity")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAndEditActivityDateFields(
    getStartDate: (LocalDate) -> Unit,
    getEndDate: (LocalDate) -> Unit,
    showBoth: Boolean
) {
    val selectedStartDate = remember { mutableStateOf<LocalDate?>(null) }
    val selectedEndDate = remember { mutableStateOf<LocalDate?>(null) }
    val showStartDatePicker = remember { mutableStateOf(false) }
    val showEndDatePicker = remember { mutableStateOf(false) }

    val formatterPattern = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val formattedStartDate = selectedStartDate.value?.format(formatterPattern) ?: ""
    val formattedEndDate = selectedEndDate.value?.format(formatterPattern) ?: ""

    // Pass the selected values back up to the caller
    selectedStartDate.value?.let { getStartDate(it) }
    selectedEndDate.value?.let { getEndDate(it) }

    OutlinedTextField(
        value = formattedStartDate,
        onValueChange = { /* Do nothing as it's read-only */ },
        label = { Text("Start Date *") },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(selectedStartDate) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showStartDatePicker.value = true
                    }
                }
            }
    )

    if (showStartDatePicker.value) {
        DateDialog(
            onDismiss = { showStartDatePicker.value = false },
            onConfirm = { datePickerState: DatePickerState ->
                datePickerState.selectedDateMillis?.let { millis ->
                    selectedStartDate.value = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                }
                showStartDatePicker.value = false
            }
        )
    }

    if (showBoth) {
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = formattedEndDate,
            onValueChange = { /* Do nothing as it's read-only */ },
            label = { Text("End Date") },
            readOnly = true,
            supportingText = { Text("Leave blank if not completed yet") },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(selectedEndDate) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showEndDatePicker.value = true
                        }
                    }
                }
        )

        if (showEndDatePicker.value) {
            DateDialog(
                onDismiss = { showEndDatePicker.value = false },
                onConfirm = { datePickerState: DatePickerState ->
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedEndDate.value = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                    }
                    showEndDatePicker.value = false
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(onDismiss: () -> Unit, onConfirm: (DatePickerState) -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(datePickerState) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}