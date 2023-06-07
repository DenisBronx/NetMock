package com.denisbrandi.netmock.resources

private external fun require(module: String): dynamic
private val fs = require("fs")

actual class Resource actual constructor(actual val filePath: String, actual val name: String) {
    private val path = "$filePath/$name"

    actual fun exists(): Boolean = fs.existsSync(path) as Boolean

    actual fun readText(): String = fs.readFileSync(path, "utf8") as String
}