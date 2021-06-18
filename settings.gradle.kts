pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.14.0"
        id("com.github.ben-manes.versions") version "0.39.0"
        id("com.github.johnrengelman.shadow") version "7.0.0"
        id("com.gorylenko.gradle-git-properties") version "2.2.4"
        id("io.micronaut.application") version "1.5.2"
        id("io.micronaut.library") version "1.5.2"
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
