package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import kotlin.test.*

class NetMockEngineTest {

    private val netMock = NetMockEngine()
    private val sut = HttpClient(netMock) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun `EXPECT mapped response`() = runTest {
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, EXPECTED_RESPONSE)

        val response = sut.request(getCompleteRequest(netMock.baseUrl))

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped responses`() = runTest {
        val expectedResponse1 = EXPECTED_RESPONSE.copy(body = "body1")
        val expectedResponse2 = EXPECTED_RESPONSE.copy(body = "body2")
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse1)
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse2)

        val response1 = sut.request(getCompleteRequest(netMock.baseUrl))
        val response2 = sut.request(getCompleteRequest(netMock.baseUrl))

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
        assertValidResponse(expectedResponse1, response1)
        assertValidResponse(expectedResponse2, response2)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped responses and default response WHEN dispatcher runs out of mocks`() = runTest {
        val expectedResponse1 = EXPECTED_RESPONSE.copy(body = "body1")
        val expectedResponse2 = EXPECTED_RESPONSE.copy(body = "body2")
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse1)
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse2)

        val response1 = sut.request(getCompleteRequest(netMock.baseUrl))
        val response2 = sut.request(getCompleteRequest(netMock.baseUrl))
        val response3 = sut.request(getCompleteRequest(netMock.baseUrl))

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
        assertValidResponse(expectedResponse1, response1)
        assertValidResponse(expectedResponse2, response2)
        assertEquals(400, response3.status.value)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT valid response for GET`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Get),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Get
            },
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for HEAD`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Head),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Head
            },
            EXPECTED_RESPONSE.copy(body = "") // body is empty in head responses
        )
    }

    @Test
    fun `EXPECT valid response for POST`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Post
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for PUT`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Put, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Put
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for DELETE`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Delete, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Delete
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for PATCH`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Patch, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Patch
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for custom method`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Custom("CUSTOM"), body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod("CUSTOM")
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    private suspend fun testResponseForMethod(
        expectedCompleteRequest: NetMockRequest,
        request: HttpRequestBuilder,
        expectedResponse: NetMockResponse
    ) {
        netMock.addMock(expectedCompleteRequest, expectedResponse)

        val response = sut.request(request)

        assertEquals(listOf(expectedCompleteRequest), netMock.interceptedRequests)
        assertValidResponse(expectedResponse, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped response WHEN request has missing fields`() = runTest {
        netMock.addMock(EXPECTED_MISSING_FIELDS_REQUEST, EXPECTED_RESPONSE)

        val response = sut.request(getRequestWithMissingFields(netMock.baseUrl))

        assertEquals(listOf(EXPECTED_MISSING_FIELDS_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT default response WHEN request is not matching`() = runTest {
        netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)

        val response = sut.request(getCompleteRequest(netMock.baseUrl))

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertEquals(400, response.status.value)
        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
    }

    @Test
    fun `EXPECT default response WHEN request body is not matching`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response = sut.request(
            getCompleteRequestBuilder(netMock.baseUrl).apply {
                method = HttpMethod.Post
                setBody("not matching body")
            }
        )

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertEquals(400, response.status.value)
        assertEquals(
            listOf(NetMockRequestResponse(expectedRequest, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
    }

    @Test
    fun `EXPECT overridden default response WHEN request is not matching and default response is overridden`() =
        runTest {
            netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)
            netMock.defaultResponse = DEFAULT_RESPONSE

            val response = sut.request(getCompleteRequest(netMock.baseUrl))

            assertTrue(netMock.interceptedRequests.isEmpty())
            assertValidResponse(DEFAULT_RESPONSE, response)
            assertEquals(
                listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
                netMock.allowedMocks
            )
        }

    private suspend fun assertValidResponse(expectedResponse: NetMockResponse, actualResponse: HttpResponse) {
        assertEquals(expectedResponse.code, actualResponse.status.value)
        assertHeaders(expectedResponse.containsHeaders, actualResponse.headers)
        assertEquals(expectedResponse.body, actualResponse.body<String>())
    }

    private fun assertHeaders(expectedHeaders: Map<String, Any>, actualHeaders: Headers) {
        expectedHeaders.forEach { (key, value) ->
            Assert.assertEquals(value, actualHeaders[key])
        }
    }

    private companion object {
        val REQUEST_BODY = """
            {
              "id": "some body id",
              "message": "some body message",
              "data": "some body text"
            }
        """
        val RESPONSE_BODY = """
            {
              "code": 200,
              "message": "some message",
              "data": "some text"
            }
        """
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            path = "/somePath",
            method = Method.Get,
            containsHeaders = mapOf("a" to "b", "c" to "d"),
            params = mapOf("1" to "2", "3" to "4")
        )
        val EXPECTED_MISSING_FIELDS_REQUEST = NetMockRequest(method = Method.Get)
        val EXPECTED_NOT_MATCHING_REQUEST = EXPECTED_COMPLETE_REQUEST.copy(
            containsHeaders = mapOf("a" to "b", "c" to "d", "e" to "f")
        )
        val EXPECTED_RESPONSE = NetMockResponse(
            code = 200,
            containsHeaders = mapOf("x" to "y"),
            body = RESPONSE_BODY
        )
        val DEFAULT_RESPONSE = NetMockResponse(code = 201, containsHeaders = mapOf("a" to "b"), body = "default")
        private fun getCompleteRequest(baseUrl: String): HttpRequestBuilder {
            return getCompleteRequestBuilder(baseUrl).apply {
                method = HttpMethod.Get
            }
        }

        private fun getRequestWithMissingFields(baseUrl: String): HttpRequestBuilder {
            return HttpRequestBuilder().apply {
                url(baseUrl)
                method = HttpMethod.Get
            }
        }

        private fun getCompleteRequestBuilder(baseUrl: String): HttpRequestBuilder {
            return HttpRequestBuilder().apply {
                url("${baseUrl}somePath?1=2&3=4")
                headers {
                    append("a", "b")
                    append("c", "d")
                }
            }
        }
    }
}