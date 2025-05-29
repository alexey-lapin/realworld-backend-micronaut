plugins {
    java
    jacoco
    id("com.diffplug.spotless")
    id("realworld.project-conventions")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    withType<JavaCompile>().configureEach {
        shouldRunAfter("spotlessJavaCheck")
    }
}
