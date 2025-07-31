package com.hollowlog.dailytracker.model

import java.time.DayOfWeek

class RepeatableInfo(
    val isDaily: Boolean,
    val isWeekly: Boolean,
    val isMonthly: Boolean,
    val isYearly: Boolean,
    val days: List<DayOfWeek>,
    val weeks: List<Int>,
    val months: List<Int>,
    val years: List<Int>
) {
}