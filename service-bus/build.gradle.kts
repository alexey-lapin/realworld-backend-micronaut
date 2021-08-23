plugins {
    id("micronaut-library-conventions")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")
    compileOnly("org.projectlombok:lombok:${libs.versions.lombok.get()}")

    annotationProcessor("io.micronaut:micronaut-graal")

    implementation("io.micronaut:micronaut-inject")
}