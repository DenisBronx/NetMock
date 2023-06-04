plugins {
    kotlin("multiplatform") version "1.8.21"
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
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
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${serialization_version}")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
            }
        }
    }
}

//publishing {
//    publications {
//        maven(MavenPublication) {
//            groupId = "com.denisbrandi.netmock"
//            artifactId = "netmock-core"
//            version = "$netmock_version"
//        }
//    }
//}