package com.hollowlog.dailytracker.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hollowlog.dailytracker.Routes
import com.hollowlog.dailytracker.model.Activity
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset
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
            topBar = { CreateActivityTopBar(navController, isEdit) },
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
fun CreateActivityTopBar(navController: NavHostController, isEdit: Boolean) {
    TopAppBar(
        title = { Text(if (isEdit) "Edit Activity" else "New Activity") },
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
    var isChecked by remember { mutableStateOf(isCheckboxMarked(isEdit, selectedActivity)) }
    var startDate by remember { mutableStateOf(if (isEdit) selectedActivity.startDate else currDate) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    // How much space should be between the components
    val SPACE_BETWEEN = Modifier.height(8.dp)

    Column(
        modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {

        Column {
            Text(text = "Activity Information")
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        }

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

        Column {
            Text(text = "Date Information")
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        }

        Spacer(SPACE_BETWEEN)

        CreateAndEditActivityDateFields(
            getStartDate = { date -> startDate = date },
            getEndDate = { date -> endDate = date },
            showBoth = isChecked,
            initialStartDate = startDate,
            initialEndDate = if (isEdit) selectedActivity.endDate else null
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

        Column {
            Text(text = "Repeating Information")
            HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        }

        RepeatingInformation()

        Spacer(SPACE_BETWEEN)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            FilledTonalButton(
                enabled = !activityNameError,
                onClick = {
                    if (activityNameText.isBlank()) {
                        activityNameError = true
                    } else {
                        // Create a new activity
                        val activity = Activity(
                            activityNameText,
                            startDate,
                            if (isChecked) endDate else startDate,
                            commentsText
                        )

                        // If the user is editing an activity, then set the id for the created activity
                        if (isEdit) {
                            activity.id = activityViewModel.selectedActivity.value.id
                            activityViewModel.updateActivity(activity)
                            activityViewModel.setSelectedActivity(activity)
                        } else {
                            activityViewModel.addActivity(activity)
                        }

                        // Return back to the selected day
                        navController.navigate(Routes.DAILY_ACTIVITY_SCREEN) {
                            popUpTo(Routes.DAILY_ACTIVITY_SCREEN) {
                                inclusive = true
                            }
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
    getEndDate: (LocalDate?) -> Unit,
    showBoth: Boolean,
    initialStartDate: LocalDate,
    initialEndDate: LocalDate?
) {
    val selectedStartDate = remember { mutableStateOf(initialStartDate) }
    val selectedEndDate = remember { mutableStateOf(initialEndDate) }
    val showStartDatePicker = remember { mutableStateOf(false) }
    val showEndDatePicker = remember { mutableStateOf(false) }

    val formatterPattern = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val formattedStartDate = selectedStartDate.value.format(formatterPattern) ?: ""
    val formattedEndDate = selectedEndDate.value?.format(formatterPattern) ?: ""

    // Pass the selected values back up to the caller
    getStartDate(selectedStartDate.value)
    getEndDate(selectedEndDate.value)

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

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = formattedEndDate,
                onValueChange = { /* Do nothing as it's read-only */ },
                label = { Text("End Date") },
                readOnly = true,
                supportingText = { Text("Leave blank if not completed yet") },
                modifier = Modifier
                    .weight(1f)
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
            IconButton(onClick = { selectedEndDate.value = null }) {
                Icon(Icons.Filled.Clear, "Clear end date")
            }
        }


        if (showEndDatePicker.value) {
            DateDialog(
                onDismiss = { showEndDatePicker.value = false },
                onConfirm = { datePickerState: DatePickerState ->
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedEndDate.value = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                    }
                    showEndDatePicker.value = false
                },
                selectableDates = OnlyFutureDates(startDate = selectedStartDate.value)
            )
        }

    }
}

@Composable
fun RepeatingInformation() {

    var doesActivityRepeat by remember { mutableStateOf(false) }
    val selectedDays = remember { mutableStateListOf<String>() }
    val dayScrollState = rememberScrollState()

    if (doesActivityRepeat) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .horizontalScroll(dayScrollState)
        ) {
            DayOfWeek.entries.forEach { day ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        onCheckedChange = {
                            if (selectedDays.contains(day.name)) {
                                selectedDays.remove(day.name)
                            } else {
                                selectedDays.add(day.name)
                            }
                        }, checked = selectedDays.contains(day.name)
                    )
                    Text(day.name)
                }
            }
        }

        Canvas(Modifier.fillMaxWidth()) {
            // Calculate how quickly the scrollbar should grow
            val max = (dayScrollState.maxValue / 1f).coerceAtLeast(size.width)
            val min = size.width.coerceAtMost(dayScrollState.maxValue / 1f)
            val scrollWidth = (dayScrollState.value / 1f) / (max / min)

            drawLine(
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = scrollWidth, y = 0f),
                color = Color.Blue,
                strokeWidth = 2.dp.toPx()
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(onCheckedChange = { doesActivityRepeat = !doesActivityRepeat }, checked = doesActivityRepeat)
        Text("This activity repeats")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    onDismiss: () -> Unit,
    onConfirm: (DatePickerState) -> Unit,
    selectableDates: SelectableDates = DatePickerDefaults.AllDates
) {
    val datePickerState = rememberDatePickerState(selectableDates = selectableDates)

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

fun isCheckboxMarked(isEdit: Boolean, activity: Activity): Boolean {
    return !(!isEdit || activity.startDate == activity.endDate)
}

@OptIn(ExperimentalMaterial3Api::class)
class OnlyFutureDates(private val startDate: LocalDate) : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= startDate.year
    }
}