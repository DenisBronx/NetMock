package com.denisbrandi.netmock.resources

import java.io.File

actual class Resource actual constructor(actual val path: String, actual val name: String) {
    private val file = File("$path/$name")

    actual fun exists(): Boolean = file.exists()

    actual fun readText(): String = file.readText()
}
