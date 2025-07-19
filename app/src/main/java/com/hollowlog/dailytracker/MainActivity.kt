package com.hollowlog.dailytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hollowlog.dailytracker.database.ActivityDatabaseProvider
import com.hollowlog.dailytracker.view.CalendarScreen
import com.hollowlog.dailytracker.view.CreateNewActivityScreen
import com.hollowlog.dailytracker.view.DailyActivityScreen
import com.hollowlog.dailytracker.view.EditActivityScreen
import com.hollowlog.dailytracker.view_model.ActivityViewModel
import java.time.LocalDate


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityDb = ActivityDatabaseProvider.getDatabase(applicationContext)
        val activityViewModel = ActivityViewModel(activityDb.dao)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Routes.DAILY_ACTIVITY_SCREEN + Routes.DAILY_ACTIVITY_SCREEN_ARGUMENTS,
                builder = {
                    composable(Routes.DAILY_ACTIVITY_SCREEN + Routes.DAILY_ACTIVITY_SCREEN_ARGUMENTS) { backStackEntry ->
                        DailyActivityScreen(navController, activityViewModel)
                    }
                    composable(Routes.CREATE_ACTIVITY_SCREEN + Routes.CREATE_ACTIVITY_SCREEN_ARGUMENTS) { backStackEntry ->
                        CreateNewActivityScreen(navController, getDateArgument(backStackEntry), activityViewModel)
                    }
                    composable(Routes.CALENDAR_SCREEN) {
                        CalendarScreen(navController, activityViewModel)
                    }

                    composable(Routes.EDIT_ACTIVITY_SCREEN + Routes.EDIT_ACTIVITY_SCREEN_ARGUMENTS) {
                        EditActivityScreen()
                    }
                })
        }
    }
}

/**
 * Get the stringified date from the Navigation arguments and convert it, or load today's date
 */
fun getDateArgument(backStackEntry: NavBackStackEntry): LocalDate {
    val dateString = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
    return LocalDate.parse(dateString)
}
