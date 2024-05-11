enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "realworld-backend-micronaut"
include("service-api")
include("service-bus")
include("service")
