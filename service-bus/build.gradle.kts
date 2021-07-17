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

    implementation("io.micronaut:micronaut-inject")
}