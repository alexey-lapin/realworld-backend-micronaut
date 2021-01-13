pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.9.0"
        id("com.github.ben-manes.versions") version "0.36.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
