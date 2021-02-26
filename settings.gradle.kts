pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.10.2"
        id("com.github.ben-manes.versions") version "0.36.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
        id("io.micronaut.application") version "1.3.4"
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
