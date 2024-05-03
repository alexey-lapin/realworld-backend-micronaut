plugins {
    java
    jacoco
    id("com.diffplug.spotless")
    id("project-conventions")
}

val hasIntTests = layout.projectDirectory.dir("src").dir("intTest").asFile.exists()

if (hasIntTests) {
    sourceSets {
        create("intTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    configurations["intTestImplementation"].extendsFrom(configurations["implementation"])
    configurations["intTestImplementation"].extendsFrom(configurations["testImplementation"])
    configurations["intTestRuntimeOnly"].extendsFrom(configurations["runtimeOnly"])
    configurations["intTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

spotless {
    val headerFile = rootProject.file("src/spotless/mit-license.java")

    java {
        licenseHeaderFile(headerFile, "(package|import|open|module) ")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks {
    if (hasIntTests) {
        register<Test>("integrationTest") {
            description = "Runs the integration tests."
            group = "verification"

            testClassesDirs = sourceSets["intTest"].output.classesDirs
            classpath = sourceSets["intTest"].runtimeClasspath

            shouldRunAfter("test")
        }

        named("check") { dependsOn("integrationTest") }
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    withType<JavaCompile>().configureEach {
        shouldRunAfter("spotlessJavaCheck")
    }
}

// Do not generate reports for individual projects
tasks.jacocoTestReport {
    enabled = false
}

// Share sources folder with other projects for aggregated JaCoCo reports
configurations.create("transitiveSourcesElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
    }
    sourceSets.main.get().java.srcDirs.forEach {
        outgoing.artifact(it)
    }
}

// Share sources folder with other projects for aggregated JaCoCo reports
configurations.create("transitiveCompiledElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("classes-folders"))
    }
    sourceSets.main.get().output.classesDirs.forEach {
        outgoing.artifact(it)
    }
}

// Share the coverage data to be aggregated for the whole product
configurations.create("coverageDataElements") {
    isVisible = false
    isCanBeResolved = false
    isCanBeConsumed = true
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
    }

    // This will cause the test task to run if the coverage data is requested by the aggregation task
    outgoing.artifact(tasks.test.map { task ->
        task.extensions.getByType<JacocoTaskExtension>().destinationFile!!
    })

    if (hasIntTests) {
        outgoing.artifact(tasks.named("integrationTest").map { task ->
            task.extensions.getByType<JacocoTaskExtension>().destinationFile!!
        })
    }
}
