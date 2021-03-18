pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.11.0"
        id("com.github.ben-manes.versions") version "0.38.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
        id("com.gorylenko.gradle-git-properties") version "2.2.4"
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
