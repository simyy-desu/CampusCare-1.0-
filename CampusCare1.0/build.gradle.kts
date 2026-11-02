// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    // Relocate build directory outside of OneDrive to avoid AccessDeniedException and sync locks
    layout.buildDirectory.set(file("C:/AndroidBuilds/${rootProject.name}/${project.name}"))
}
