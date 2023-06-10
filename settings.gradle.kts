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
include(":netmock-integration-test")
includeBuild("convention-plugins")
include(":netmock-resources")
