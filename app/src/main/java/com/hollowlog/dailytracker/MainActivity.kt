package com.hollowlog.dailytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hollowlog.dailytracker.database.ActivityDatabaseProvider
import com.hollowlog.dailytracker.view.CalendarScreen
import com.hollowlog.dailytracker.view.CreateAndEditActivityScreen
import com.hollowlog.dailytracker.view.DailyActivityScreen
import com.hollowlog.dailytracker.view_model.ActivityViewModel

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
                startDestination = Routes.DAILY_ACTIVITY_SCREEN,
                builder = {
                    composable(Routes.DAILY_ACTIVITY_SCREEN) {
                        DailyActivityScreen(navController, activityViewModel)
                    }
                    composable(
                        Routes.CREATE_ACTIVITY_SCREEN + "/{isEdit}",
                        arguments = listOf(navArgument("isEdit") { type = NavType.BoolType })
                    ) { backStackEntry ->
                        val isEdit = backStackEntry.arguments?.getBoolean("isEdit", false)
                        CreateAndEditActivityScreen(navController, activityViewModel, isEdit!!)
                    }
                    composable(Routes.CALENDAR_SCREEN) {
                        CalendarScreen(navController, activityViewModel)
                    }
                })
        }
    }
}
