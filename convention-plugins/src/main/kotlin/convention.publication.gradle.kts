import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.Sign
import java.util.*

plugins {
    `maven-publish`
    signing
}

// Stub secrets to let the project sync and build without the publication values set up
ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["signing.secretKey"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

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
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun getExtraString(name: String) = ext[name]?.toString()

// Stub javadoc.jar (required by Maven Central)
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {

    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {

        artifact(javadocJar)

        pom {
            name.set("NetMock")
            description.set("Network test library for JVM, Kotlin, Android and Kotlin Multiplatform")
            url.set("https://github.com/DenisBronx/NetMock")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("DenisBronx")
                    name.set("Denis Brandi")
                    email.set("dnsbrnd@gmail.com")
                }
            }

            scm {
                url.set("https://github.com/DenisBronx/NetMock")
            }
        }
    }
}

/**
 * 🔥 CRITICAL FIX FOR GRADLE 8+
 *
 * Ensure publish tasks depend on signing tasks.
 * This removes the implicit dependency error.
 */
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(tasks.withType<Sign>())
}

/**
 * Signing configuration
 */
signing {

    val signingKeyId = getExtraString("signing.keyId")
    val signingKey = getExtraString("signing.secretKey")
    val signingPassword = getExtraString("signing.password")

    val hasSigningKeys =
        !signingKeyId.isNullOrBlank() &&
            !signingKey.isNullOrBlank() &&
            !signingPassword.isNullOrBlank()

    if (hasSigningKeys) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
