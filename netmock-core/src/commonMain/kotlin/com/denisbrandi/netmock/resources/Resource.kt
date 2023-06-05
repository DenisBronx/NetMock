package com.denisbrandi.netmock.resources

const val RESOURCE_PATH = "./src/commonTest/resources"

expect class Resource(name: String) {
    val name: String

    fun exists(): Boolean
    fun readText(): String
}

fun readFromResources(filePath: String): String {
    return Resource(filePath).readText()
}