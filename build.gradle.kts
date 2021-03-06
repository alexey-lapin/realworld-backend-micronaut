plugins {
    java
    id("com.github.ben-manes.versions")
    id("com.diffplug.spotless")
}

val enableJacoco = project.hasProperty("enableJacoco")
val jacocoClassesDir = file("$buildDir/jacoco/classes")

description = "Real world backend API built in Micronaut"

allprojects {
    apply(plugin = "idea")
    apply(plugin = "com.diffplug.spotless")

    version = "0.1.1"

    if (enableJacoco) {
        apply(plugin = "jacoco")
        configure<JacocoPluginExtension> {
            toolVersion = Versions.jacoco
        }
    }

    repositories {
        mavenCentral()
    }
}

subprojects {
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

        configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        afterEvaluate {
            if (enableJacoco) {
                val jarTask = tasks["jar"] as Jar
                val extractJar by tasks.registering(Copy::class) {
                    from(zipTree(jarTask.archiveFile))
                    into(jacocoClassesDir)
                    include("**/*.class")
                    includeEmptyDirs = false
                    onlyIf { jarTask.enabled }
                }
                jarTask.finalizedBy(extractJar)
            }
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

        named("build").get().shouldRunAfter("spotlessJava")
    }
}

if (enableJacoco) {
    tasks {
        val jacocoMerge by registering(JacocoMerge::class) {
            subprojects.forEach { project ->
                executionData(fileTree("dir" to "${project.buildDir}/jacoco", "include" to "*.exec"))
                dependsOn(project.tasks.withType<Test>())
            }
        }
        register<JacocoReport>("jacocoRootReport") {
            dependsOn(jacocoMerge)
            subprojects.forEach { project ->
                project.pluginManager.withPlugin("java") {
                    sourceDirectories.from(project.the<SourceSetContainer>()["main"].allSource.srcDirs)
                }
            }
            classDirectories.from(files(jacocoClassesDir))
            executionData(jacocoMerge.get().destinationFile)
            reports {
                html.isEnabled = true
                xml.isEnabled = true
                csv.isEnabled = false
            }
        }
    }
}

tasks {
    dependencyUpdates {
        checkConstraints = true
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("Release candidate")
                    }
                }
            }
        }
    }
}
