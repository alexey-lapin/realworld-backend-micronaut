plugins {
    base
    alias(libs.plugins.versions)
    alias(libs.plugins.release)
    id("realworld.jacoco-aggregation")
}

description = "Real world backend API built in Micronaut"
version = scmVersion.version

dependencies {
    implementation(project(":service"))
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
