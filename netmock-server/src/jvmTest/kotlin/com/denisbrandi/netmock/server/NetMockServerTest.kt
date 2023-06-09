package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.Method
import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockRequestResponse
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.resources.readFromJvmResources
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NetMockServerTest {
    @get:Rule
    val netMock = NetMockServerRule()

    private val sut = OkHttpClient.Builder()
        .addInterceptor(netMock.server.interceptor)
        .build()

    @Test
    fun `EXPECT mapped response WHEN directed to localhost`() {
        val sut = OkHttpClient.Builder().build()
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(requestUrl = "${netMock.server.baseUrl}somePath?1=2&3=4")
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response = sut.newCall(getCompleteRequest(netMock.server.baseUrl)).execute()

        assertEquals(listOf(expectedRequest), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped response`() {
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, EXPECTED_RESPONSE)

        val response = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped responses`() {
        val expectedResponse1 = EXPECTED_RESPONSE.copy(body = "body1")
        val expectedResponse2 = EXPECTED_RESPONSE.copy(body = "body2")
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse1)
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse2)

        val response1 = sut.newCall(getCompleteRequest(BASE_URL)).execute()
        val response2 = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertEquals(
            listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST),
            netMock.interceptedRequests
        )
        assertValidResponse(expectedResponse1, response1)
        assertValidResponse(expectedResponse2, response2)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped responses and default response WHEN dispatcher runs out of mocks`() {
        val expectedResponse1 = EXPECTED_RESPONSE.copy(body = "body1")
        val expectedResponse2 = EXPECTED_RESPONSE.copy(body = "body2")
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse1)
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, expectedResponse2)

        val response1 = sut.newCall(getCompleteRequest(BASE_URL)).execute()
        val response2 = sut.newCall(getCompleteRequest(BASE_URL)).execute()
        val response3 = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertEquals(
            listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST),
            netMock.interceptedRequests
        )
        assertValidResponse(expectedResponse1, response1)
        assertValidResponse(expectedResponse2, response2)
        assertEquals(400, response3.code)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT valid response for GET`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Get),
            getCompleteRequestBuilder(BASE_URL).get().build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for HEAD`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Head),
            getCompleteRequestBuilder(BASE_URL).head().build(),
            EXPECTED_RESPONSE.copy(body = "") // body is empty in head responses
        )
    }

    @Test
    fun `EXPECT valid response for POST`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).post(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for PUT`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Put, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).put(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for DELETE`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Delete, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).delete(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for PATCH`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Patch, body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).patch(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for custom method`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.Custom("CUSTOM"), body = REQUEST_BODY),
            getCompleteRequestBuilder(BASE_URL).method(
                "CUSTOM",
                REQUEST_BODY.toRequestBody()
            ).build(),
            EXPECTED_RESPONSE
        )
    }

    private fun testResponseForMethod(
        expectedCompleteRequest: NetMockRequest,
        request: Request,
        expectedResponse: NetMockResponse
    ) {
        netMock.addMock(expectedCompleteRequest, expectedResponse)

        val response = sut.newCall(request).execute()

        assertEquals(listOf(expectedCompleteRequest), netMock.interceptedRequests)
        assertValidResponse(expectedResponse, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT mapped response WHEN request has missing fields`() {
        netMock.addMock(EXPECTED_MISSING_FIELDS_REQUEST, EXPECTED_RESPONSE)

        val response = sut.newCall(getRequestWithMissingFields()).execute()

        assertEquals(listOf(EXPECTED_MISSING_FIELDS_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT default response WHEN request is not matching`() {
        netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)

        val response = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertEquals(400, response.code)
        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
    }

    @Test
    fun `EXPECT default response WHEN request body is not matching`() {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response = sut.newCall(
            getCompleteRequestBuilder(BASE_URL).post("not matching body".toRequestBody()).build()
        ).execute()

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertEquals(400, response.code)
        assertEquals(
            listOf(NetMockRequestResponse(expectedRequest, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
    }

    @Test
    fun `EXPECT overridden default response WHEN request is not matching and default response is overridden`() {
        netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)
        netMock.defaultResponse = DEFAULT_RESPONSE

        val response = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertValidResponse(DEFAULT_RESPONSE, response)
        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
    }

    private fun assertValidResponse(expectedResponse: NetMockResponse, actualResponse: Response) {
        assertEquals(expectedResponse.code, actualResponse.code)
        expectedResponse.containsHeaders.forEach {
            assertTrue(actualResponse.networkResponse!!.headers.contains(it.key to it.value))
        }
        assertEquals(expectedResponse.body, actualResponse.body!!.string())
    }

    private companion object {
        const val BASE_URL = "https://google.com/"
        val REQUEST_BODY = readFromJvmResources("request_body.json")
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            requestUrl = "https://google.com/somePath?1=2&3=4",
            method = Method.Get,
            containsHeaders = mapOf("a" to "b", "c" to "d")
        )
        val EXPECTED_MISSING_FIELDS_REQUEST = NetMockRequest(requestUrl = "https://google.com/", method = Method.Get)
        val EXPECTED_NOT_MATCHING_REQUEST = EXPECTED_COMPLETE_REQUEST.copy(
            containsHeaders = mapOf("a" to "b", "c" to "d", "e" to "f")
        )
        val EXPECTED_RESPONSE = NetMockResponse(
            code = 200,
            containsHeaders = mapOf("x" to "y"),
            body = readFromJvmResources("response_body.json")
        )
        val DEFAULT_RESPONSE =
            NetMockResponse(code = 201, containsHeaders = mapOf("a" to "b"), body = "default")

        private fun getCompleteRequest(baseUrl: String): Request {
            return getCompleteRequestBuilder(baseUrl).get().build()
        }

        private fun getRequestWithMissingFields(): Request {
            return Request.Builder()
                .get()
                .url(BASE_URL)
                .build()
        }

        private fun getCompleteRequestBuilder(baseUrl: String): Request.Builder {
            return Request.Builder()
                .headers(Headers.headersOf("a", "b", "c", "d"))
                .url("${baseUrl}somePath?1=2&3=4")
        }
    }
}
