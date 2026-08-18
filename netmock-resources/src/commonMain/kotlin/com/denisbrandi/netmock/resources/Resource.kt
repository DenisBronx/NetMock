package com.denisbrandi.netmock.resources

import com.goncalossilva.resources.Resource

@Deprecated(message = "Use `readFromResources` instead", replaceWith = ReplaceWith("readFromResources(fileName)"))
fun readFromCommonResources(fileName: String): String {
    return Resource(fileName).readText()
}

fun readFromResources(fileName: String): String {
    return Resource(fileName).readText()
}

@Deprecated(message = "Use `readFromResources` instead", replaceWith = ReplaceWith("readFromResources(fileName)"))
fun readFromJvmResources(fileName: String): String {
    return Resource(fileName).readText()
}

@Deprecated(message = "Use `readFromResources` instead", replaceWith = ReplaceWith("readFromResources(fileName)"))
fun readFromNativeResources(fileName: String): String {
    return Resource(fileName).readText()
}
