plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("convention.publication")
}

apply(from = "../coverage/coverageReport.gradle")

publishing {
    publications.withType<MavenPublication> {
        groupId = "io.github.denisbronx.netmock"
        artifactId = "netmock-server"
        version = libs.versions.netmock.get()
    }
}

kotlin {
    jvmToolchain(17)
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    sourceSets {
        val jvmMain by getting {
            sourceSets {
                dependencies {
                    api(project(":netmock-core"))
                    api(project(":netmock-resources"))
                    implementation(project.dependencies.platform(libs.okhttp.bom))
                    implementation(libs.okhttp)
                    implementation(libs.mockwebserver)
                    implementation(libs.junit)
                }
            }
        }
    }
}
