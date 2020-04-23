pluginManagement {
    plugins {
        id("com.github.ben-manes.versions") version "0.28.0"
        id("com.diffplug.gradle.spotless") version "3.28.1"
        id("com.github.johnrengelman.shadow") version "5.2.0"
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
