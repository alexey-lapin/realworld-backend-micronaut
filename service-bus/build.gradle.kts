plugins {
    id("micronaut-library-conventions")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    annotationProcessor("io.micronaut:micronaut-graal")

    compileOnly("com.google.code.findbugs:jsr305")

    implementation("io.micronaut:micronaut-inject")
}