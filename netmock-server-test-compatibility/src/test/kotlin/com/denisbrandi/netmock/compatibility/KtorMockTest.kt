package com.denisbrandi.netmock.compatibility

import com.denisbrandi.netmock.Method
import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.resources.readFromResources
import com.denisbrandi.netmock.server.NetMockServerRule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class KtorMockTest {
    @get:Rule
    val netMock = NetMockServerRule()
    private val sut = HttpClient(OkHttp) {
        engine {
            addInterceptor(netMock.server.interceptor)
        }
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `EXPECT GET response WHEN localhost`() = runTest {
        val sut = HttpClient(CIO.create()) {
            install(ContentNegotiation) {
                json()
            }
        }
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(requestUrl = "${netMock.server.baseUrl}somePath?1=2&3=4")
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response = sut.get(getUrl(netMock.server.baseUrl), withHeaders())

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
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
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
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
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post, body = REQUEST_BODY_RAW),
            EXPECTED_RESPONSE
        )

        val response = sut.post(getUrl()) {
            addHeaders()
            setBody(REQUEST_OBJECT)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
    }

    @Test
    fun `EXPECT PUT response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Put, body = REQUEST_BODY_RAW),
            EXPECTED_RESPONSE
        )

        val response = sut.put(getUrl()) {
            addHeaders()
            setBody(REQUEST_OBJECT)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
    }

    @Test
    fun `EXPECT DELETE response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Delete, body = REQUEST_BODY_RAW),
            EXPECTED_RESPONSE
        )

        val response = sut.delete(getUrl()) {
            addHeaders()
            setBody(REQUEST_OBJECT)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
    }

    @Test
    fun `EXPECT PATCH response`() = runTest {
        netMock.addMock(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Patch, body = REQUEST_BODY_RAW),
            EXPECTED_RESPONSE
        )

        val response = sut.patch(getUrl()) {
            addHeaders()
            setBody(REQUEST_OBJECT)
        }

        assertEquals(200, response.status.value)
        assertHeaders(EXPECTED_RESPONSE_HEADERS, response.headers)
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
    }

    private fun getUrl(baseUrl: String = BASE_URL) = "${baseUrl}somePath?1=2&3=4"

    private fun withHeaders(): HttpRequestBuilder.() -> Unit = {
        addHeaders()
    }

    private fun HttpRequestBuilder.addHeaders() {
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            append("a", "b")
            append("c", "d")
        }
    }

    private fun assertHeaders(expectedHeaders: Map<String, Any>, actualHeaders: Headers) {
        expectedHeaders.forEach { (key, value) ->
            assertEquals(value, actualHeaders[key])
        }
    }

    @Serializable
    private data class RequestObject(val id: String, val message: String, val data: String)

    @Serializable
    private data class ResponseObject(val code: Int, val message: String, val data: String)

    private companion object {
        const val BASE_URL = "https://google.com/"
        val REQUEST_BODY_RAW = readFromResources("request_body.json")
        val REQUEST_OBJECT = RequestObject("some body id", "some body message", "some body text")
        val RESPONSE_BODY_RAW = readFromResources("response_body.json")
        val RESPONSE_OBJECT = ResponseObject(200, "some message", "some text")
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            requestUrl = "https://google.com/somePath?1=2&3=4",
            method = Method.Get,
            mandatoryHeaders = mapOf("a" to "b", "c" to "d", "Content-Type" to "application/json")
        )
        val EXPECTED_RESPONSE_HEADERS =
            mapOf("x" to "y", "Content-Type" to "application/json")
        val EXPECTED_RESPONSE =
            NetMockResponse(
                code = 200,
                containsHeaders = EXPECTED_RESPONSE_HEADERS,
                body = RESPONSE_BODY_RAW
            )
    }
}
