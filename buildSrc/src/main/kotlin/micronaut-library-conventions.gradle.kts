plugins {
    id("java-conventions")
    id("io.micronaut.library")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

micronaut {
    version.set(libs.findVersion("mn").get().displayName)
    processing {
        annotations("com.github.al.realworld.*")
    }
}
