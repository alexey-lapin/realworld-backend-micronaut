plugins {
    id("java-conventions")
    id("io.micronaut.application")
    id("com.github.johnrengelman.shadow")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

micronaut {
    version.set(libs.findVersion("mn").get().displayName)
    processing {
        annotations("com.github.al.realworld.*")
    }
}
