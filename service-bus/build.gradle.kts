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

    implementation("io.micronaut:micronaut-inject")
}