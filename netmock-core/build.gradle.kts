import java.util.Properties
import kotlin.apply
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("signing")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

apply(from = "../coverage/coverageReport.gradle")

publishing {
    publications.withType<MavenPublication> {
        groupId = "io.github.denisbronx.netmock"
        artifactId = "netmock-core"
        version = libs.versions.netmock.get()
    }
}

kotlin {
    jvmToolchain(17)
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.serialization)
                implementation(libs.kermit)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["signing.secretKey"] = null
ext["signingInMemoryKey"] = null
ext["signingInMemoryKeyPassword"] = null
ext["mavenCentralUsername"] = null
ext["mavenCentralPassword"] = null
ext["signing.secretKeyRingFile"] = null

// Load secrets from secrets.properties or environment (CI)
val secretPropsFile = project.rootProject.file("secrets.properties")
if (secretPropsFile.exists()) {
    Properties().apply {
        secretPropsFile.reader().use { load(it) }
    }.forEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKey"] = System.getenv("SIGNING_SECRET_KEY")
    ext["signingInMemoryKey"] = System.getenv("SIGNING_KEY_ID")
    ext["signingInMemoryKeyPassword"] = System.getenv("SIGNING_PASSWORD")
    ext["mavenCentralUsername"] = System.getenv("MAVEN_CENTRAL_USERNAME")
    ext["mavenCentralPassword"] = System.getenv("MAVEN_CENTRAL_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "netmock-core", version.toString())

    pom {
        name = "NetMock"
        description = "Network test library for JVM, Kotlin, Android and Kotlin Multiplatform"
        inceptionYear = "2023"
        url = "https://github.com/DenisBronx/NetMock"
        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "DenisBronx"
                name = "Denis Brandi"
                email = "dnsbrnd@gmail.com"
            }
        }
        scm {
            url = "https://github.com/DenisBronx/NetMock"
            connection = "scm:git:git://github.com/DenisBronx/NetMock.git"
            developerConnection = "scm:git:ssh://git@github.com:DenisBronx/NetMock.git"
        }
    }
}
