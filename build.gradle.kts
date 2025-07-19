// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Hilt
    // id("com.google.dagger.hilt.android") version "2.57" apply false

    // Room DB
    val room_version = "2.7.2"
    id("androidx.room") version room_version apply false
}