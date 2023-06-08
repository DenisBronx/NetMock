package com.denisbrandi.netmock.resources

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JvmResourceTest {

    private val sut = Resource(COMMON_RESOURCES_PATH, FILE_PATH)

    @JsName("text_exists")
    @Test
    fun `EXPECT text and exists WHEN file exists`() {
        assertTrue(sut.exists())
        assertEquals(EXPECTED_TEXT, sut.readText())
        assertEquals(EXPECTED_TEXT, readFromCommonResources(FILE_PATH))
    }

    @JsName("notExists")
    @Test
    fun `EXPECT false WHEN file does not exists`() {
        val sut = Resource(COMMON_RESOURCES_PATH, "a")
        assertFalse(sut.exists())
    }

    private companion object {
        const val FILE_PATH = "request_body.json"
        const val EXPECTED_TEXT = """{
  "id": "some body id",
  "message": "some body message",
  "data": "some body text"
}"""
    }
}
