pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "NetMock"
include(":netmock-core")
include(":netmock-engine")
include(":netmock-server")
include(":netmock-server-test-compatibility")
includeBuild("convention-plugins")
