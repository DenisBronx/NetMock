plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

apply(from = "../coverage/overallCoverageReport.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(project(":netmock-server"))
    testImplementation(project(":netmock-engine"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.serialization)
    testImplementation(libs.coroutines.test)

    //retrofit
    testImplementation(platform(libs.okhttp.bom))
    testImplementation(libs.okhttp)
    testImplementation(libs.retrofit)
    testImplementation(libs.retrofit.converter.scalars)
    testImplementation(libs.retrofit.serialization)

    //ktor
    testImplementation(libs.ktor)
    testImplementation(libs.ktor.okhttp)
    testImplementation(libs.ktor.cio)
    testImplementation(libs.ktor.content.negotiation)
    testImplementation(libs.ktor.serialization)
}
