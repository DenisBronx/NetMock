import java.util.*
import org.gradle.kotlin.dsl.*

plugins {
    id("com.vanniktech.maven.publish")
    signing
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["signing.secretKey"] = null
ext["signingInMemoryKeyId"] = null
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
    ext["signingInMemoryKeyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signingInMemoryKey"] = System.getenv("GPG_KEY_CONTENTS")
    ext["signingInMemoryKeyPassword"] = System.getenv("SIGNING_PASSWORD")
    ext["mavenCentralUsername"] = System.getenv("MAVEN_CENTRAL_USERNAME")
    ext["mavenCentralPassword"] = System.getenv("MAVEN_CENTRAL_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), project.name, version.toString())

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
                url = "https://github.com/DenisBronx/NetMock"
                email = "dnsbrnd@gmail.com"
                organization = "Denis Brandi"
                organizationUrl = "https://github.com/DenisBronx/NetMock"
            }
        }
        scm {
            url = "https://github.com/DenisBronx/NetMock"
            connection = "scm:git:git://github.com/DenisBronx/NetMock.git"
            developerConnection = "scm:git:ssh://git@github.com:DenisBronx/NetMock.git"
        }
    }
}
