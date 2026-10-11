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

    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mn.micronaut.test.junit5)
    testImplementation(mn.assertj.core)
}

testing {
    suites {
        withType<JvmTestSuite>().configureEach {
            // keep the engine and launcher on the version the Micronaut platform manages
            useJUnitJupiter(mn.versions.junit5)
        }
    }
}