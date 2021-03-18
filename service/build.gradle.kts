import com.gorylenko.GenerateGitPropertiesTask
import com.gorylenko.GitPropertiesPluginExtension

plugins {
    application
    id("com.github.johnrengelman.shadow")
    id("com.gorylenko.gradle-git-properties")
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

idea {
    module {
        testSourceDirs.addAll(sourceSets["intTest"].java.srcDirs)
        testResourceDirs.addAll(sourceSets["intTest"].resources.srcDirs)
        scopes["TEST"]!!["plus"]!!.add(configurations["intTestCompile"])
    }
}

configurations.all {
    resolutionStrategy.force("org.liquibase:liquibase-core:${Versions.liquibase}")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")

    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut:micronaut-validation")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.security:micronaut-security")

    implementation(project(":service-bus"))
    implementation(project(":service-api"))

    implementation(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation("io.jsonwebtoken:jjwt-api:${Versions.jwt}")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.slf4j:jul-to-slf4j:${Versions.slf4j}")

    runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${Versions.jwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jwt}")

    testAnnotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")

    testImplementation(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    "intTestAnnotationProcessor"(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    "intTestAnnotationProcessor"("io.micronaut:micronaut-inject-java")

    "intTestAnnotationProcessor"("org.projectlombok:lombok:${Versions.lombok}")
    "intTestCompileOnly"("org.projectlombok:lombok:${Versions.lombok}")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

configure<GitPropertiesPluginExtension> {
    keys = listOf("git.branch", "git.commit.id", "git.commit.id.abbrev", "git.commit.time")
}

application {
    mainClassName = "com.github.al.realworld.App"
}

tasks {
    shadowJar {
        mergeServiceFiles()
    }

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

    named("check") { dependsOn("integrationTest") }

    // used by Heroku
    register("stage") {
        dependsOn("clean", "build")
        mustRunAfter("clean")
    }
}