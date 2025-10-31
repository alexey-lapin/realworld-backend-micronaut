import com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentSelectionWithCurrent

plugins {
    base
    alias(libs.plugins.versions)
    alias(libs.plugins.release)
    id("jacoco-report-aggregation")
}

description = "Real world backend API built in Micronaut"
version = scmVersion.version

dependencies {
    jacocoAggregation(project(":service"))
}

reporting {
    reports {
        val testJacocoCoverageReport by creating(JacocoCoverageReport::class) {
            testSuiteName = "test"
        }
        val intTestJacocoCoverageReport by creating(JacocoCoverageReport::class) {
            testSuiteName = "intTest"
        }
    }
}

tasks {
    register("jacocoReport") {
        reporting.reports.withType<JacocoCoverageReport>().configureEach {
            dependsOn(reportTask)
        }
    }

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
