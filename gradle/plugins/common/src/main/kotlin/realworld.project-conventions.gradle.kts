import pl.allegro.tech.build.axion.release.domain.VersionConfig

// one version for the whole build: axion lives on the root only, so release tasks aren't duplicated per module
if (project == rootProject) {
    pluginManager.apply("pl.allegro.tech.build.axion-release")
}

group = "com.github.alexey-lapin.realworld"
version = rootProject.extensions.getByType(VersionConfig::class).version

repositories {
    mavenCentral()
}
