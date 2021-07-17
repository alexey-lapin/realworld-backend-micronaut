import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.gorylenko.GenerateGitPropertiesTask
import com.gorylenko.GitPropertiesPluginExtension

import io.micronaut.gradle.graalvm.NativeImageTask
import org.gradle.internal.os.OperatingSystem

plugins {
    application
    id("com.github.johnrengelman.shadow")
    id("com.gorylenko.gradle-git-properties")
    id("io.micronaut.application")
}

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

//idea {
//    module {
//        testSourceDirs.addAll(sourceSets["intTest"].java.srcDirs)
//        testResourceDirs.addAll(sourceSets["intTest"].resources.srcDirs)
//        scopes["TEST"]!!["plus"]!!.add(configurations["intTestCompile"])
//    }
//}

micronaut {
    version.set(libs.versions.mn.get())
    processing {
        annotations("com.github.al.realworld.*")
    }
}

configurations.all {
    resolutionStrategy.force("org.liquibase:liquibase-core:${libs.versions.liquibase.get()}")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")
    compileOnly("org.projectlombok:lombok:${libs.versions.lombok.get()}")

    annotationProcessor("io.micronaut.openapi:micronaut-openapi")

    implementation(project(":service-bus"))
    implementation(project(":service-api"))

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation("io.jsonwebtoken:jjwt-api:${libs.versions.jwt.get()}")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.slf4j:jul-to-slf4j:${libs.versions.slf4j.get()}")

    runtimeOnly("ch.qos.logback:logback-classic:${libs.versions.logback.get()}")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${libs.versions.jwt.get()}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${libs.versions.jwt.get()}")

    testAnnotationProcessor("io.micronaut:micronaut-inject-java")

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.assertj:assertj-core:${libs.versions.assertj.get()}")
    testImplementation("org.mockito:mockito-core:${libs.versions.mockito.get()}")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    "intTestAnnotationProcessor"(platform("io.micronaut:micronaut-bom:${libs.versions.mn.get()}"))
    "intTestAnnotationProcessor"("io.micronaut:micronaut-inject-java")

    "intTestAnnotationProcessor"("org.projectlombok:lombok:${libs.versions.lombok.get()}")
    "intTestCompileOnly"("org.projectlombok:lombok:${libs.versions.lombok.get()}")
}

configure<GitPropertiesPluginExtension> {
    keys = listOf("git.branch", "git.commit.id", "git.commit.id.abbrev", "git.commit.time")
}

application {
    mainClassName = "com.github.al.realworld.App"
}

tasks {
    register<Test>("integrationTest") {
        description = "Runs the integration tests."
        group = "verification"

        testClassesDirs = sourceSets["intTest"].output.classesDirs
        classpath = sourceSets["intTest"].runtimeClasspath
        shouldRunAfter("test")
    }

    register<Copy>("copyOpenApiConfig") {
        from(rootProject.file("openapi.properties"))
        destinationDir = rootProject.buildDir
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
            listOf("-Dmicronaut.openapi.config.file=${rootProject.file("build/openapi.properties")}")
        dependsOn("copyOpenApiConfig")
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${rootProject.name}-${archiveVersion.get()}.${archiveExtension.get()}")
    }

    withType<NativeImageTask> {
        val os = OperatingSystem.current()
        imageName.set("${rootProject.name}-${version}-${os.nativePrefix}${os.executableSuffix}")
        verbose(true)
        if (os.isWindows) {
            executable("native-image.cmd")
        }
    }

    named("check") { dependsOn("integrationTest") }

    // used by Heroku
    register("stage") {
        dependsOn("clean", "build")
        mustRunAfter("clean")
    }
}