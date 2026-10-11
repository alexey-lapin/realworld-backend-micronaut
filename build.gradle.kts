import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentSelectionWithCurrent

plugins {
    alias(libs.plugins.versions)
    id("jacoco-report-aggregation")
    id("realworld.project-conventions")
    // declared once here so every module loads them from the same classloader; applied per
    // module, the GraalVM build service they share cannot be wired across modules, which
    // breaks task creation such as IDE sync of collectReachabilityMetadata
    alias(libs.plugins.micronaut.application) apply false
    alias(libs.plugins.micronaut.library) apply false
    alias(libs.plugins.shadow) apply false
}

description = "Real world backend API built in Micronaut"

dependencies {
    jacocoAggregation(project(":service"))
}

reporting {
    reports {
        // one per suite is all the plugin offers; jacocoReport below merges them
        create<JacocoCoverageReport>("testCodeCoverageReport") { testSuiteName = "test" }
        create<JacocoCoverageReport>("integrationTestCodeCoverageReport") { testSuiteName = "integrationTest" }
    }
}

tasks.register<JacocoReport>("jacocoReport") {
    group = "verification"
    description = "Aggregates coverage from every module and every test suite."

    reporting.reports.withType<JacocoCoverageReport>().forEach { report ->
        val task = report.reportTask
        sourceDirectories.from(task.map { it.sourceDirectories })
        classDirectories.from(task.map { it.classDirectories })
        executionData.from(task.map { it.executionData })
    }

    reports {
        xml.required = true
        html.required = true
    }
}

tasks {
    dependencyUpdates {
        checkConstraints = true
        resolutionStrategy {
            componentSelection {
                all { selection: ComponentSelectionWithCurrent ->
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(selection.candidate.version) }
                    if (rejected) {
                        selection.reject("Release candidate")
                    }
                }
            }
        }
    }
}
