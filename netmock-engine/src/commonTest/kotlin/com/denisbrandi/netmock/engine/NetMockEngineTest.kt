package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlin.js.JsName
import kotlin.test.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable

class NetMockEngineTest {

    private val netMock = NetMockEngine()
    private val sut = HttpClient(netMock) {
        install(ContentNegotiation) {
            json()
        }
    }

    @JsName("mappedResponse")
    @Test
    fun `EXPECT mapped response`() = runTest {
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, EXPECTED_RESPONSE)

        val response = sut.request(getCompleteRequest(BASE_URL))

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @JsName("mappedResponse_withCustomMatcher")
    @Test
    fun `EXPECT mapped response WHEN using custom matcher`() = runTest {
        netMock.addMockWithCustomMatcher(
            requestMatcher = { it.requestUrl == EXPECTED_COMPLETE_REQUEST.requestUrl },
            response = EXPECTED_RESPONSE
        )

        val response = sut.request(getCompleteRequest(BASE_URL))

        assertEquals(1, netMock.interceptedRequests.size)
        assertInterceptedRequestWithCustomMatcher(
            EXPECTED_COMPLETE_REQUEST,
            netMock.interceptedRequests.first()
        )
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    private fun assertInterceptedRequestWithCustomMatcher(
        expectedRequest: NetMockRequest,
        interceptedRequest: NetMockRequest
    ) {
        assertEquals(expectedRequest.requestUrl, interceptedRequest.requestUrl)
        assertEquals(expectedRequest.method, interceptedRequest.method)
        assertEquals(expectedRequest.body, interceptedRequest.body)
        assertTrue(
            interceptedRequest.mandatoryHeaders.toList()
                .containsAll(expectedRequest.mandatoryHeaders.toList())
        )
    }

    @JsName("mappedResponses")
    @Test
    fun `EXPECT mapped responses`() = runTest {
        val expectedResponse1 = EXPECTED_RESPONSE.copy(body = "body1")
        val expectedResponse2 = EXPECTED_RESPONSE.copy(body = "body2")
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse1)
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse2)

        val response1 = sut.request(getCompleteRequest(BASE_URL))
        val response2 = sut.request(getCompleteRequest(BASE_URL))

        assertEquals(
            listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST),
            netMock.interceptedRequests
        )
        assertValidResponse(expectedResponse1, response1)
        assertValidResponse(expectedResponse2, response2)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @JsName("mappedResponseAndDefaultResponse_noMoreMocks")
    @Test
    fun `EXPECT mapped responses and default response WHEN dispatcher runs out of mocks`() =
        runTest {
            val expectedResponse1 = EXPECTED_RESPONSE.copy(body = "body1")
            val expectedResponse2 = EXPECTED_RESPONSE.copy(body = "body2")
            netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse1)
            netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse2)

            val response1 = sut.request(getCompleteRequest(BASE_URL))
            val response2 = sut.request(getCompleteRequest(BASE_URL))
            val response3 = sut.request(getCompleteRequest(BASE_URL))

            assertEquals(
                listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST),
                netMock.interceptedRequests
            )
            assertValidResponse(expectedResponse1, response1)
            assertValidResponse(expectedResponse2, response2)
            assertEquals(400, response3.status.value)
            assertTrue(netMock.allowedMocks.isEmpty())
        }

    @JsName("validGET")
    @Test
    fun `EXPECT valid response for GET`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Get),
            getCompleteRequestBuilder(BASE_URL).apply {
                method = HttpMethod.Get
            },
            EXPECTED_RESPONSE
        )
    }

    @JsName("validHEAD")
    @Test
    fun `EXPECT valid response for HEAD`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Head),
            getCompleteRequestBuilder(BASE_URL).apply {
                method = HttpMethod.Head
            },
            EXPECTED_RESPONSE.copy(body = "") // body is empty in head responses
        )
    }

    @JsName("validPOST")
    @Test
    fun `EXPECT valid response for POST`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).apply {
                method = HttpMethod.Post
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @JsName("validPUT")
    @Test
    fun `EXPECT valid response for PUT`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Put, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).apply {
                method = HttpMethod.Put
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @JsName("validDELETE")
    @Test
    fun `EXPECT valid response for DELETE`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Delete, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).apply {
                method = HttpMethod.Delete
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @JsName("validPATCH")
    @Test
    fun `EXPECT valid response for PATCH`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Patch, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).apply {
                method = HttpMethod.Patch
                setBody(REQUEST_BODY)
            },
            EXPECTED_RESPONSE
        )
    }

    @JsName("validCUSTOM")
    @Test
    fun `EXPECT valid response for custom method`() = runTest {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Custom("CUSTOM"), body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).apply {
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

    @JsName("mappedResponse_missingFields")
    @Test
    fun `EXPECT mapped response WHEN request has missing fields`() = runTest {
        netMock.addMock(EXPECTED_MISSING_FIELDS_REQUEST, EXPECTED_RESPONSE)

        val response = sut.request(getRequestWithMissingFields(BASE_URL))

        assertEquals(listOf(EXPECTED_MISSING_FIELDS_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @JsName("defaultResponse_noMatch")
    @Test
    fun `EXPECT default response WHEN request is not matching`() = runTest {
        netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)

        val response = sut.request(getCompleteRequest(BASE_URL))

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertEquals(400, response.status.value)
        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
    }

    @JsName("defaultResponse_bodyNotMatching")
    @Test
    fun `EXPECT default response WHEN request body is not matching`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response = sut.request(
            getCompleteRequestBuilder(BASE_URL).apply {
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

    @JsName("overriddenDefaultResponse_noMatch")
    @Test
    fun `EXPECT overridden default response WHEN request is not matching and default response is overridden`() =
        runTest {
            netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)
            netMock.defaultResponse = DEFAULT_RESPONSE

            val response = sut.request(getCompleteRequest(BASE_URL))

            assertTrue(netMock.interceptedRequests.isEmpty())
            assertValidResponse(DEFAULT_RESPONSE, response)
            assertEquals(
                listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
                netMock.allowedMocks
            )
        }

    @JsName("jsonResponse")
    @Test
    fun `EXPECT valid response WHEN using json`() = runTest {
        val expectedCompleteRequest =
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Custom("CUSTOM"), body = REQUEST_BODY)
        val request = getCompleteRequestBuilder(BASE_URL).apply {
            method = HttpMethod("CUSTOM")
            setBody(REQUEST_OBJECT)
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }
        val expectedResponse = EXPECTED_JSON_RESPONSE
        netMock.addMock(expectedCompleteRequest, expectedResponse)

        val response = sut.request(request)

        assertEquals(listOf(expectedCompleteRequest), netMock.interceptedRequests)
        assertEquals(expectedResponse.code, response.status.value)
        assertHeaders(expectedResponse.mandatoryHeaders, response.headers)
        assertEquals(RESPONSE_OBJECT, response.body<ResponseObject>())
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    private suspend fun assertValidResponse(
        expectedResponse: NetMockResponse,
        actualResponse: HttpResponse
    ) {
        assertEquals(expectedResponse.code, actualResponse.status.value)
        assertHeaders(expectedResponse.mandatoryHeaders, actualResponse.headers)
        assertEquals(expectedResponse.body, actualResponse.body<String>())
    }

    private fun assertHeaders(expectedHeaders: Map<String, Any>, actualHeaders: Headers) {
        expectedHeaders.forEach { (key, value) ->
            assertEquals(value, actualHeaders[key] as Any)
        }
    }

    @Serializable
    private data class RequestObject(val id: String, val message: String, val data: String)

    @Serializable
    private data class ResponseObject(val code: Int, val message: String, val data: String)

    private companion object {
        const val BASE_URL = "http://google.com"
        val REQUEST_OBJECT = RequestObject("some body id", "some body message", "some body text")
        val RESPONSE_OBJECT = ResponseObject(200, "some message", "some text")
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            requestUrl = "http://google.com/somePath?1=2&3=4",
            method = Method.Get,
            mandatoryHeaders = mapOf("a" to "b", "c" to "d")
        )
        val EXPECTED_MISSING_FIELDS_REQUEST =
            NetMockRequest(requestUrl = "http://google.com/", method = Method.Get)
        val EXPECTED_NOT_MATCHING_REQUEST = EXPECTED_COMPLETE_REQUEST.copy(
            mandatoryHeaders = mapOf("a" to "b", "c" to "d", "e" to "f")
        )
        val EXPECTED_RESPONSE = NetMockResponse(
            code = 200,
            mandatoryHeaders = mapOf("x" to "y"),
            body = RESPONSE_BODY
        )
        val EXPECTED_JSON_RESPONSE = NetMockResponse(
            code = 200,
            mandatoryHeaders = mapOf("x" to "y", "Content-Type" to "application/json"),
            body = RESPONSE_BODY
        )
        val DEFAULT_RESPONSE =
            NetMockResponse(code = 201, mandatoryHeaders = mapOf("a" to "b"), body = "default")

        private fun getCompleteRequest(baseUrl: String): HttpRequestBuilder {
            return getCompleteRequestBuilder(baseUrl).apply {
                method = HttpMethod.Get
            }
        }

        private fun getRequestWithMissingFields(baseUrl: String): HttpRequestBuilder {
            return HttpRequestBuilder().apply {
                url("$baseUrl/")
                method = HttpMethod.Get
            }
        }

        private fun getCompleteRequestBuilder(baseUrl: String): HttpRequestBuilder {
            return HttpRequestBuilder().apply {
                url("$baseUrl/somePath?1=2&3=4")
                headers {
                    append("a", "b")
                    append("c", "d")
                }
            }
        }
    }
}
