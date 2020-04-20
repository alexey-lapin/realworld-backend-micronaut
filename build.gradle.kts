plugins {
    java
    id("com.github.ben-manes.versions")
    id("com.diffplug.gradle.spotless")
}

val enableJacoco = project.hasProperty("enableJacoco")
val jacocoClassesDir = file("$buildDir/jacoco/classes")

allprojects {
    apply(plugin = "idea")
    apply(plugin = "com.diffplug.gradle.spotless")

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

        afterEvaluate {
            if (enableJacoco) {
                val jarTask = tasks["jar"] as Jar
                val extractJar by tasks.registering(Copy::class) {
                    from(zipTree(jarTask.archivePath))
                    into(jacocoClassesDir)
                    include("**/*.class")
                    includeEmptyDirs = false
                    onlyIf { jarTask.enabled }
                }
                jarTask.finalizedBy(extractJar)
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
