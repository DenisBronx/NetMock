plugins {
    kotlin("multiplatform") version "1.8.21"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
    id("convention.publication")
}

publishing {
    publications.withType<MavenPublication> {
        groupId = "io.github.denisbronx.netmock"
        artifactId = "netmock-engine"
        version = netmock_version
    }
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    sourceSets {
        commonMain {
            sourceSets {
                dependencies {
                    api(project(":netmock-core"))
                    api("io.ktor:ktor-client-core:$ktor_version")
                    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                    api("io.ktor:ktor-client-mock:$ktor_version")
                    implementation("co.touchlab:kermit:$kermit_version")
                }
            }
        }
        commonTest {
            sourceSets {
                dependencies {
                    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                    implementation("org.jetbrains.kotlin:kotlin-test")
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_test_version")
                }
            }
        }
    }
}