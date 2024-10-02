@file:OptIn(ExperimentalForeignApi::class)

package com.denisbrandi.netmock.resources

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import platform.posix.FILE
import platform.posix.SEEK_END
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.rewind

actual class Resource actual constructor(actual val path: String, actual val name: String) {
    private val file: CPointer<FILE>? = fopen("$path/$name", "r")

    actual fun exists(): Boolean = file != null

    actual fun readText(): String {
        fseek(file, 0, SEEK_END)
        val size = ftell(file)
        rewind(file)

        return memScoped {
            val tmp = allocArray<ByteVar>(size)
            fread(tmp, sizeOf<ByteVar>().convert(), size.convert(), file)
            tmp.toKString()
        }
    }
}
