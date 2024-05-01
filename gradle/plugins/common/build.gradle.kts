plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.bmuschko:gradle-docker-plugin:6.7.0")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("io.micronaut.gradle:micronaut-gradle-plugin:2.0.8")
    implementation("pl.allegro.tech.build:axion-release-plugin:1.17.2")
}