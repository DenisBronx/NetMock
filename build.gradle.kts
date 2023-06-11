plugins {
    id("com.android.application") version android_plugin_version apply false
    id("com.android.library") version android_plugin_version apply false
    id("org.jetbrains.kotlin.android") version kotlin_version apply false
    id("org.jetbrains.kotlin.jvm") version kotlin_version apply false
    id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
    id("org.jetbrains.kotlin.multiplatform") version kotlin_version apply false
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version nexus_publish_version
}
