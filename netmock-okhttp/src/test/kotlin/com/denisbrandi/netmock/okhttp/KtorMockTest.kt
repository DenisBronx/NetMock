package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.*
import com.denisbrandi.netmock.assets.readFromResources
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.assertEquals

class KtorMockTest {
    @get:Rule
    val netMock = OkHttpRule()
    private val sut = HttpClient(CIO.create()) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `EXPECT GET response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST,
            EXPECTED_RESPONSE
        )

        val response = sut.get(getUrl(), withHeaders())

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_BODY, response.body<String>())
    }

    @Test
    fun `EXPECT HEAD response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Head),
            EXPECTED_RESPONSE.copy(body = "")
        )

        val response = sut.head(getUrl(), withHeaders())

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals("", response.body<String>())
    }

    @Test
    fun `EXPECT POST response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post, body = REQUEST_BODY),
            EXPECTED_RESPONSE
        )

        val response = sut.post(getUrl()) {
            addHeaders()
            setBody(REQUEST_BODY)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_BODY, response.body<String>())
    }

    @Test
    fun `EXPECT PUT response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Put, body = REQUEST_BODY),
            EXPECTED_RESPONSE
        )

        val response = sut.put(getUrl()) {
            addHeaders()
            setBody(REQUEST_BODY)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_BODY, response.body<String>())
    }

    @Test
    fun `EXPECT DELETE response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Delete, body = REQUEST_BODY),
            EXPECTED_RESPONSE
        )

        val response = sut.delete(getUrl()) {
            addHeaders()
            setBody(REQUEST_BODY)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_BODY, response.body<String>())
    }

    @Test
    fun `EXPECT PATCH response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Patch, body = REQUEST_BODY),
            EXPECTED_RESPONSE
        )

        val response = sut.patch(getUrl()) {
            addHeaders()
            setBody(REQUEST_BODY)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_BODY, response.body<String>())
    }

    private fun getUrl() = "${netMock.baseUrl}somePath?1=2&3=4"

    private fun withHeaders(): HttpRequestBuilder.() -> Unit = {
        addHeaders()
    }

    private fun HttpRequestBuilder.addHeaders() {
        headers {
            append("a", "b")
            append("c", "d")
        }
    }

    private fun assertHeaders(expectedHeaders: Map<String, Any>, actualHeaders: Headers) {
        expectedHeaders.forEach { (key, value) ->
            assertEquals(value, actualHeaders[key])
        }
    }

    private companion object {
        val REQUEST_BODY = readFromResources("request_body.json")
        val RESPONSE_BODY = readFromResources("response_body.json")
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            path = "/somePath",
            method = Method.Get,
            containsHeaders = mapOf("a" to "b", "c" to "d"),
            params = mapOf("1" to "2", "3" to "4")
        )
        val EXPECTED_RESPONSE_HEADERS = mapOf("x" to "y")
        val EXPECTED_RESPONSE =
            NetMockResponse(code = 200, containsHeaders = EXPECTED_RESPONSE_HEADERS, body = RESPONSE_BODY)
    }
}