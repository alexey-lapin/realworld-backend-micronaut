plugins {
    java
    id("com.github.ben-manes.versions")
    id("com.diffplug.gradle.spotless")
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "com.diffplug.gradle.spotless")

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    pluginManager.withPlugin("java") {

        spotless {
            val headerFile = rootProject.file("src/spotless/mit-license.java")

            java {
                licenseHeaderFile(headerFile, "(package|import|open|module) ")
                removeUnusedImports()
                trimTrailingWhitespace()
                endWithNewline()
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}