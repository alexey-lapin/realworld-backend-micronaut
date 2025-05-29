import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.gorylenko.GenerateGitPropertiesTask
import com.gorylenko.GitPropertiesPluginExtension
import org.graalvm.buildtools.gradle.tasks.BuildNativeImageTask
import org.gradle.internal.os.OperatingSystem

plugins {
    id("realworld.java-conventions")
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.shadow)
    alias(libs.plugins.git.properties)
}

micronaut {
    importMicronautPlatform = false
    runtime("netty")
    processing {
        annotations("com.github.al.realworld.*")
    }
}

application {
    mainClass.set("com.github.al.realworld.App")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("intTest") {
            configurations {
                named(sources.implementationConfigurationName) {
                    extendsFrom(getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME))
                }
            }
            dependencies {
                annotationProcessor(mn.lombok)
                compileOnly(mn.lombok)

                annotationProcessor(mn.micronaut.inject.java)

                implementation(project())

                implementation(mn.micronaut.http.client)
                implementation(mn.micronaut.test.junit5)
                implementation(mn.assertj.core)
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

dependencies {
    annotationProcessor(mn.lombok)
    compileOnly(mn.lombok)

    compileOnly(mn.micronaut.openapi.annotations)

    implementation(project(":service-bus"))
    implementation(project(":service-api"))

    implementation(mn.micronaut.data.hibernate.jpa)
    implementation(mn.micronaut.hibernate.jpa)
    implementation(mn.micronaut.jackson.databind)
    implementation(mn.micronaut.jdbc.hikari)
    implementation(mn.micronaut.liquibase)
    implementation(mn.micronaut.management)
    implementation(mn.micronaut.micrometer.registry.prometheus)
    implementation(mn.micronaut.reactor)
    implementation(mn.micronaut.security.jwt)
    implementation(mn.micronaut.validation)

    implementation(mn.slf4j.jul.to.slf4j)

    runtimeOnly(mn.logback.classic)
    runtimeOnly(mn.h2)

    testImplementation(mn.micronaut.test.junit5)
    testImplementation(mn.assertj.core)
    testImplementation(mn.mockito.core)
}

configure<GitPropertiesPluginExtension> {
    keys = listOf(
        "git.branch",
        "git.commit.id",
        "git.commit.id.abbrev",
        "git.commit.time"
    )
}

graalvmNative {
    toolchainDetection.set(true)
    binaries {
        named("main") {
            imageName.set(rootProject.name)
            buildArgs.add("--verbose")
        }
    }
}

tasks {
    register<Copy>("copyOpenApiConfig") {
        from(rootProject.file("openapi.properties"))
        destinationDir = project.layout.buildDirectory.asFile.get()
        expand(
            mapOf(
                "name" to rootProject.name,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        )
    }

    named<GenerateGitPropertiesTask>("generateGitProperties") {
        onlyIf { !source.isEmpty }
    }

    named<JavaCompile>("compileJava") {
        options.isFork = true
        options.forkOptions.jvmArgs =
            listOf("-Dmicronaut.openapi.config.file=${project.layout.buildDirectory.file("openapi.properties").get()}")
        dependsOn("copyOpenApiConfig")
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${rootProject.name}-${archiveVersion.get()}.${archiveExtension.get()}")
    }

    named<DockerBuildImage>("dockerBuildNative") {
        val registry = System.getenv("CR_REGISTRY")!!
        val namespace = System.getenv("CR_NAMESPACE")!!
        images.set(
            listOf(
                "${registry}/${namespace}/${rootProject.name}:latest",
                "${registry}/${namespace}/${rootProject.name}:${project.version}"
            )
        )
    }

    val writeArtifactFile by registering {
        doLast {
            val outputDirectory = getByName<BuildNativeImageTask>("nativeCompile").outputDirectory
            outputDirectory.get().asFile.mkdirs()
            outputDirectory.file("gradle-artifact.txt")
                .get().asFile
                .writeText("${rootProject.name}-${project.version}-${platform()}")
        }
    }

    named("nativeCompile") {
        finalizedBy(writeArtifactFile)
    }

    named("check") {
        dependsOn(testing.suites.named("intTest"))
    }

}

fun platform(): String {
    val os = OperatingSystem.current()
    val arc = System.getProperty("os.arch")
    return when {
        OperatingSystem.current().isWindows -> "windows-${arc}"
        OperatingSystem.current().isLinux -> "linux-${arc}"
        OperatingSystem.current().isMacOsX -> "darwin-${arc}"
        else -> os.nativePrefix
    }
}
