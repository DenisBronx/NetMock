import java.util.*
import org.gradle.kotlin.dsl.*

plugins {
    id("com.vanniktech.maven.publish")
    signing
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
