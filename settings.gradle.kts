pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("realworld.settings")
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
