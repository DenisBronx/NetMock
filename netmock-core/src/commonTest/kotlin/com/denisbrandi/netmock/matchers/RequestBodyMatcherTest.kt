package com.denisbrandi.netmock.matchers

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RequestBodyMatcherTest {

    private val sut = RequestBodyMatcher

    @JsName("true_null")
    @Test
    fun `EXPECT true WHEN null and empty`() {
        assertTrue(sut.isMatchingTheBody(null, ""))
    }

    @JsName("true_empty")
    @Test
    fun `EXPECT true WHEN empty and empty`() {
        assertTrue(sut.isMatchingTheBody("", ""))
    }

    @JsName("true_equal")
    @Test
    fun `EXPECT true WHEN body values are equal`() {
        assertTrue(sut.isMatchingTheBody("body", "body"))
    }

    @JsName("true_sameJsonObject")
    @Test
    fun `EXPECT true WHEN json objects are equal`() {
        assertTrue(
            sut.isMatchingTheBody(Json.encodeToString(REQUEST_BODY_JSON), EXPECTED_JSON_BODY)
        )
    }

    @JsName("true_sameJsonArray")
    @Test
    fun `EXPECT true WHEN json arrays are equal`() {
        assertTrue(
            sut.isMatchingTheBody(
                Json.encodeToString(REQUEST_BODY_JSON_ARRAY),
                EXPECTED_JSON_ARRAY_BODY
            )
        )
    }

    @JsName("false_noMatch")
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
