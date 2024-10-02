plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(project(":netmock-server"))
    testImplementation(project(":netmock-engine"))
    kover(project(":netmock-core"))
    kover(project(":netmock-engine"))
    kover(project(":netmock-server"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.serialization)
    testImplementation(libs.coroutines.test)

    //retrofit
    testImplementation(platform("com.squareup.okhttp3:okhttp-bom:${libs.versions.okhttp.get()}"))
    testImplementation("com.squareup.okhttp3:okhttp")
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
