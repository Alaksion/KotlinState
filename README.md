# KotlinState
Kotlin library designed to create a state management structure.


## Download

```kotlin


// Add to your build.gradle.kts file
dependencies {
    val latestVersion = "1.0.0-alpha-04"

    implementation("io.github.alaksion:ui-state:$latestVersion")
    implementation("io.github.alaksion:ui-event:$latestVersion")
}

```

## Usage
KotlinState two different API's to handle user interaction, `UiState` to serve as state class for views (usually a data class) and `UiEvent` to handle one shot events from a presenter to the consumer view.

# UiState
UiState is a `StateFlow<T>` which holds a data class with 2 properties: `T` and `UiStateType`.
