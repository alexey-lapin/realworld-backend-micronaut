enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "5.14.2"
        id("com.github.ben-manes.versions") version "0.39.0"
        id("com.github.johnrengelman.shadow") version "7.0.0"
        id("com.gorylenko.gradle-git-properties") version "2.2.4"
        id("io.micronaut.application") version "2.0.2"
        id("io.micronaut.library") version "2.0.2"
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("jwt", "0.11.2")
            version("liquibase", "4.4.2")
            version("logback", "1.2.4")
            version("lombok", "1.18.20")
            version("mn", "2.5.9")
            version("slf4j", "1.7.32")

            version("assertj", "3.20.2")
            version("mockito", "3.11.2")

            version("jacoco", "0.8.5")
        }
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
