plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    id("maven-publish")
    id("convention.publication")
}

publishing {
    publications.withType<MavenPublication> {
        groupId = "io.github.denisbronx.netmock"
        artifactId = "netmock-engine"
        version = libs.versions.netmock.get()
    }
}

kotlin {
    jvm {
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
                    api(project(":netmock-resources"))
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
                    implementation("org.jetbrains.kotlin:kotlin-test")
                    implementation(libs.coroutines.test)
                }
            }
        }
    }
}
