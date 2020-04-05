dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")

    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")

    implementation(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    implementation(project(":service-bus"))
    implementation("io.micronaut:micronaut-http")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("javax.validation:validation-api")
}