plugins {
//    id("com.github.ben-manes.versions")
    alias(libs.plugins.release)
    id("jacoco-report-aggregation")
}

description = "Real world backend API built in Micronaut"

//dependencies {
//    implementation(project(":service"))
//}

//tasks {
//    dependencyUpdates {
//        checkConstraints = true
//        resolutionStrategy {
//            componentSelection {
//                all {
//                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
//                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
//                        .any { it.matches(candidate.version) }
//                    if (rejected) {
//                        reject("Release candidate")
//                    }
//                }
//            }
//        }
//    }
//}
