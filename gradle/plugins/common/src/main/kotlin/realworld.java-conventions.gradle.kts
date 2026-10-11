plugins {
    java
    jacoco
    id("com.diffplug.spotless")
    id("realworld.project-conventions")
}

val javaVersion = 25

configure<JavaPluginExtension> {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    // fails the build if a newer API leaks in, whatever JDK the toolchain resolves
    options.release = javaVersion
}

spotless {
    val headerFile = rootProject.file("src/spotless/mit-license.java")

    java {
        targetExclude("build/generated/**/*.java")
        licenseHeaderFile(headerFile, "(package|import|open|module) ")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    withType<JavaCompile>().configureEach {
        shouldRunAfter("spotlessJavaCheck")
        options.compilerArgs.add("-parameters")
    }
}

// coverage is aggregated in the root project
tasks.jacocoTestReport {
    enabled = false
}
