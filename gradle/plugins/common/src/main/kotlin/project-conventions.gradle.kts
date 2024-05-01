//import pl.allegro.tech.build.axion.release.domain.VersionConfig

plugins {
    id("pl.allegro.tech.build.axion-release") apply false
}

//version = rootProject.extensions.getByType(VersionConfig::class).version
version = "0.1.0"

repositories {
    mavenCentral()
}