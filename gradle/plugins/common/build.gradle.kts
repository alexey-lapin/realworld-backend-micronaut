plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gradle.plugin.micronaut)
    implementation(libs.gradle.plugin.release)
    implementation(libs.gradle.plugin.spotless)
}