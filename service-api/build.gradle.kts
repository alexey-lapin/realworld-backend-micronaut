plugins {
    id("micronaut-library-conventions")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    annotationProcessor("io.micronaut:micronaut-graal")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")

    compileOnly("com.google.code.findbugs:jsr305")

    implementation(project(":service-bus"))

    implementation("io.micronaut:micronaut-http")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.validation:jakarta.validation-api")
}