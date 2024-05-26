plugins {
    id("realworld.java-conventions")
    alias(libs.plugins.micronaut.library)
}

micronaut {
    importMicronautPlatform = false
    processing {
        annotations("com.github.al.realworld.*")
    }
}

dependencies {
    annotationProcessor(mn.lombok)
    compileOnly(mn.lombok)
}