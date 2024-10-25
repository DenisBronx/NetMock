plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kover)
    id("maven-publish")
    alias(libs.plugins.nexus.publish)
}

dependencies {
    kover(project(":netmock-core"))
    kover(project(":netmock-engine"))
    kover(project(":netmock-server"))
}
