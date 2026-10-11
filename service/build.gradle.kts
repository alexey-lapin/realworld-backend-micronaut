import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.gorylenko.GenerateGitPropertiesTask
import com.gorylenko.GitPropertiesPluginExtension

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
        withType<JvmTestSuite>().configureEach {
            // keep the engine and launcher on the version the Micronaut platform manages
            useJUnitJupiter(mn.versions.junit5)
        }

        register<JvmTestSuite>("integrationTest") {
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
            targets.configureEach {
                testTask.configure { shouldRunAfter(tasks.test) }
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
        // CI sets these; defaults keep configuration from failing elsewhere, e.g. in an IDE sync
        val registry = System.getenv("CR_REGISTRY") ?: "ghcr.io"
        val namespace = System.getenv("CR_NAMESPACE") ?: "local"
        images.set(
            listOf(
                "${registry}/${namespace}/${rootProject.name}:latest",
                "${registry}/${namespace}/${rootProject.name}:${project.version}"
            )
        )
    }

    named("check") {
        dependsOn(testing.suites.named("integrationTest"))
    }

}
