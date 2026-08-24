@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kmp.resources)
    id("convention.publication")
}

apply(from = "../coverage/coverageReport.gradle")

publishing {
    publications.withType<MavenPublication> {
        groupId = "io.github.denisbronx.netmock"
        artifactId = "netmock-engine"
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
    mingwX64()
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js(IR) {
        browser()
        nodejs()
    }
    wasmJs {
        browser()
        nodejs()
    }
    sourceSets {
        commonMain {
            sourceSets {
                dependencies {
                    api(project(":netmock-core"))
                    api(libs.ktor)
                    implementation(libs.ktor.serialization)
                    api(libs.ktor.mock)
                }
            }
        }
        commonTest {
            sourceSets {
                dependencies {
                    implementation(libs.ktor.content.negotiation)
                    implementation(libs.kotlin.test)
                    implementation(libs.coroutines.test)
                    implementation(project(":netmock-resources"))
                }
            }
        }
    }
}
