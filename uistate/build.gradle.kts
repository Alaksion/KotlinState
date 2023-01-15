plugins {
    id("java-library")
    kotlin("jvm")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("KotlinState") {
                from(components["java"])
                groupId = "com.github.alaksion"
                artifactId = "kotlin-state"
                version = "1"
            }
        }

        repositories {
            maven {
                name = "KotlinState"
                url = uri(layout.buildDirectory.dir("repo"))
            }
        }
    }
}