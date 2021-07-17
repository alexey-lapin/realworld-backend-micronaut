plugins {
    id("io.micronaut.library")
}

micronaut {
    version.set(libs.versions.mn.get())
    processing {
        annotations("com.github.al.realworld.*")
    }
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")
    compileOnly("org.projectlombok:lombok:${libs.versions.lombok.get()}")

    annotationProcessor("io.micronaut:micronaut-graal")

    implementation(project(":service-bus"))
    implementation("io.micronaut:micronaut-http")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("javax.validation:validation-api")
}