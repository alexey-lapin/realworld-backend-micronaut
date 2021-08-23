plugins {
    id("java-conventions")
    id("io.micronaut.application")
}

micronaut {
    version.set("2.5.11")
    processing {
        annotations("com.github.al.realworld.*")
    }
}
