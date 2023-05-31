package com.denisbrandi.netmock.assets

import java.io.File

fun readFromResources(filePath: String): String {
    return File("src/test/resources/$filePath").bufferedReader().readText()
}