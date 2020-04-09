plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")

    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut:micronaut-validation")
    annotationProcessor("io.micronaut:micronaut-security")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")

    implementation(project(":service-bus"))
    implementation(project(":service-api"))

    implementation(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    implementation("io.micronaut.configuration:micronaut-jdbc-tomcat")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-security")
    implementation("io.micronaut:micronaut-validation")

    implementation("io.jsonwebtoken:jjwt-api:${Versions.jwt}")
    implementation("javax.annotation:javax.annotation-api")

    runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${Versions.jwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jwt}")

    testAnnotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")

    testImplementation(platform("io.micronaut:micronaut-bom:${Versions.mn}"))
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.assertj:assertj-core:${Versions.assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "com.github.al.realworld.App"
}

tasks {
    shadowJar {
        mergeServiceFiles()
    }

    // used by Heroku
    register("stage") {
        dependsOn("clean", "build")
        mustRunAfter("clean")
    }
}