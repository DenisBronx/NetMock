plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
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
                    implementation(project.dependencies.platform("com.squareup.okhttp3:okhttp-bom:${libs.versions.okhttp.get()}"))
                    implementation("com.squareup.okhttp3:okhttp")
                    implementation("com.squareup.okhttp3:mockwebserver3")
                    implementation(libs.junit)
                }
            }
        }
    }
}
