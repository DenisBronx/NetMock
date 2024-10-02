plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kover)
    id("maven-publish")
    id("convention.publication")
}

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
                    implementation(platform("com.squareup.okhttp3:okhttp-bom:${libs.versions.okhttp.get()}"))
                    implementation("com.squareup.okhttp3:okhttp")
                    implementation("com.squareup.okhttp3:mockwebserver")
                }
            }
        }
    }
}
