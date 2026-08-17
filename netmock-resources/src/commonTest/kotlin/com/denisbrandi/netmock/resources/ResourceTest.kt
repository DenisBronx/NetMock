package com.denisbrandi.netmock.resources

import com.goncalossilva.resources.Resource
import kotlin.test.*

class ResourceTest {

    private val sut = Resource(FILE_PATH)

    @Test
    fun `EXPECT text and exists WHEN file exists`() {
        assertTrue(sut.exists())
        assertEquals(EXPECTED_TEXT, sut.readText())
        assertEquals(EXPECTED_TEXT, readFromResources(FILE_PATH))
    }

    @Test
    fun `EXPECT false WHEN file does not exists`() {
        val sut = Resource("a")

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
