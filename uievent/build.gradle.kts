plugins {
    id("java-library")
    kotlin("jvm")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

group = "br.com.alaksion"
version = "1.0.0-alpha-01"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}