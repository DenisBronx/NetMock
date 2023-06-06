package com.denisbrandi.netmock.server.resources

import java.io.File

fun readFromResources(filePath: String): String {
    return File("src/test/resources/$filePath").bufferedReader().readText()
}