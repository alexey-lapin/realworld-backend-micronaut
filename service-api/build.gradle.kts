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

    annotationProcessor(mn.micronaut.validation.processor)

    implementation(project(":service-bus"))

    implementation(mn.micronaut.http)
    implementation(mn.jackson.annotations)
    implementation(mn.validation)
}