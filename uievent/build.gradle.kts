plugins {
    id("java-library")
    kotlin("jvm")
    id("com.vanniktech.maven.publish") version "0.23.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}