package com.hollowlog.dailytracker

/**
 * Navigation routes to the different screens within the application.
 */
object Routes {
    const val DAILY_ACTIVITY_SCREEN = "daily-activity-screen"
    const val DAILY_ACTIVITY_SCREEN_ARGUMENTS = "/{date}"

    const val CREATE_ACTIVITY_SCREEN = "CreateActivityScreen"
    const val CREATE_ACTIVITY_SCREEN_ARGUMENTS = "/{date}"

    const val CALENDAR_SCREEN = "CalendarScreen"

    const val EDIT_ACTIVITY_SCREEN = "edit-activity-screen"
    const val EDIT_ACTIVITY_SCREEN_ARGUMENTS = "/{activity-id}"
}