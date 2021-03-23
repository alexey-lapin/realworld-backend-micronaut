plugins {
    id("io.micronaut.library")
}

micronaut {
    version.set(Versions.mn)
    processing {
        annotations("com.github.al.realworld.*")
    }
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")

    annotationProcessor("io.micronaut:micronaut-graal")

    implementation(project(":service-bus"))
    implementation("io.micronaut:micronaut-http")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("javax.validation:validation-api")
}