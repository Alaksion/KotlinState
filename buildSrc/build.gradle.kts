plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("publishable-module") {
            id = "br.com.alaksion.publishable.module"
            implementationClass = "buildSrc.PublishModulePlugin"
        }
    }
}
