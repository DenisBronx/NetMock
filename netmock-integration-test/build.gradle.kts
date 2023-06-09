plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.kover") version kover_version
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
    testImplementation("junit:junit:$junit_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_test_version")

    //retrofit
    testImplementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp_version"))
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    testImplementation("com.squareup.retrofit2:converter-scalars:$retrofit_version")
    testImplementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$retrofit_serialization_version")

    //ktor
    testImplementation("io.ktor:ktor-client-core:$ktor_version")
    testImplementation("io.ktor:ktor-client-okhttp:$ktor_version")
    testImplementation("io.ktor:ktor-client-cio:$ktor_version")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}
