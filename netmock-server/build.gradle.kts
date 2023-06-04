plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

publishing {
    publications.withType<MavenPublication> {
        groupId = "com.denisbrandi.netmock"
        artifactId = "netmock-server"
        version = netmock_version
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":netmock-core"))
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp_version"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:mockwebserver")
}