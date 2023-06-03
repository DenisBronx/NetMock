package com.denisbrandi.netmock.matchers

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.*

class RequestBodyMatcherTest {

    private val sut = RequestBodyMatcher

    @Test
    fun `EXPECT true WHEN body values are equal`() {
        assertTrue(sut.isMatchingTheBody("body", "body"))
    }

    @Test
    fun `EXPECT true WHEN json objects are equal`() {
        assertTrue(sut.isMatchingTheBody(Json.encodeToString(REQUEST_BODY_JSON), EXPECTED_JSON_BODY))
    }

    @Test
    fun `EXPECT true WHEN json arrays are equal`() {
        assertTrue(sut.isMatchingTheBody(Json.encodeToString(REQUEST_BODY_JSON_ARRAY), EXPECTED_JSON_ARRAY_BODY))
    }

    @Test
    fun `EXPECT false WHEN body values do not match`() {
        assertFalse(sut.isMatchingTheBody("<>", "body"))
    }

    @Serializable
    private data class JsonBody(val id: String, val message: String, val data: String)

    private companion object {
        const val EXPECTED_JSON_BODY = """
    {
        "id": "some body id",
        "message": "some body message",
        "data": "some body text"
    }
"""
        const val EXPECTED_JSON_ARRAY_BODY = """
    [
        {
            "id": "some body id",
            "message": "some body message",
            "data": "some body text"
        },
        {
            "id": "some body id 2",
            "message": "some body message 2",
            "data": "some body text 2"
        }
    ]
"""
        val REQUEST_BODY_JSON = JsonBody(
            id = "some body id",
            message = "some body message",
            data = "some body text"
        )
        val REQUEST_BODY_JSON_ARRAY = listOf(
            REQUEST_BODY_JSON,
            JsonBody(
                id = "some body id 2",
                message = "some body message 2",
                data = "some body text 2"
            )
        )
    }
}