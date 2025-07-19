package com.hollowlog.dailytracker.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hollowlog.dailytracker.ui.theme.TrackerApplicationTheme

@Composable
fun EditActivityScreen() {
    TrackerApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {

                }
            }
        )
    }
}