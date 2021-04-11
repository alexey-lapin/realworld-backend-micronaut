pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.12.0"
        id("com.github.ben-manes.versions") version "0.38.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
        id("com.gorylenko.gradle-git-properties") version "2.2.4"
        id("io.micronaut.application") version "1.4.5"
        id("io.micronaut.library") version "1.4.5"
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
