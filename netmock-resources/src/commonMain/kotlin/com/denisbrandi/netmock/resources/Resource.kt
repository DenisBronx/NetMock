package com.denisbrandi.netmock.resources

const val RESOURCES_PATH = "./src/test/resources"
const val COMMON_RESOURCES_PATH = "./src/commonTest/resources"
const val JVM_RESOURCES_PATH = "./src/jvmTest/resources"
const val NATIVE_RESOURCES_PATH = "./src/nativeTest/resources"

expect class Resource(path: String, name: String) {
    val path: String
    val name: String

    fun exists(): Boolean
    fun readText(): String
}

fun readFromCommonResources(fileName: String): String {
    return Resource(COMMON_RESOURCES_PATH, fileName).readText()
}

fun readFromResources(fileName: String): String {
    return Resource(RESOURCES_PATH, fileName).readText()
}

fun readFromJvmResources(fileName: String): String {
    return Resource(JVM_RESOURCES_PATH, fileName).readText()
}

fun readFromNativeResources(fileName: String): String {
    return Resource(NATIVE_RESOURCES_PATH, fileName).readText()
}
