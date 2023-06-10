plugins {
    kotlin("multiplatform") version "1.8.21"
    id("maven-publish")
    id("convention.publication")
    id("org.jetbrains.kotlinx.kover") version kover_version
}

publishing {
    publications.withType<MavenPublication> {
        groupId = "io.github.denisbronx.netmock"
        artifactId = "netmock-server"
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
    sourceSets {
        val jvmMain by getting {
            sourceSets {
                dependencies {
                    api(project(":netmock-core"))
                    api(project(":netmock-resources"))
                    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp_version"))
                    implementation("com.squareup.okhttp3:okhttp")
                    implementation("com.squareup.okhttp3:mockwebserver")
                }
            }
        }
    }
}
