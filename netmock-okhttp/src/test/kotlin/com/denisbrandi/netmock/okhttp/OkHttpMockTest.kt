package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.*
import org.junit.Assert.*

class OkHttpMockTest {
    private val netMock = OkHttpMock()
    private val sut = OkHttpClient.Builder().build()

    @Test
    fun `EXPECT mapped response`() {
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, EXPECTED_RESPONSE)

        val response = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()

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

        val response1 = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()
        val response2 = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
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

        val response1 = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()
        val response2 = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()
        val response3 = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()

        assertEquals(listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST), netMock.interceptedRequests)
        assertValidResponse(expectedResponse1, response1)
        assertValidResponse(expectedResponse2, response2)
        assertEquals(400, response3.code)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT valid response for GET`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.GET),
            getCompleteRequestBuilder(netMock.baseUrl).get().build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for HEAD`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.HEAD),
            getCompleteRequestBuilder(netMock.baseUrl).head().build(),
            EXPECTED_RESPONSE.copy(body = "") // body is empty in head responses
        )
    }

    @Test
    fun `EXPECT valid response for POST`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.POST, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).post(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for PUT`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.PUT, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).put(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for DELETE`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.DELETE, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).delete(REQUEST_BODY.toRequestBody()).build(),
            EXPECTED_RESPONSE
        )
    }

    @Test
    fun `EXPECT valid response for PATCH`() {
        testResponseForMethod(
            EXPECTED_COMPLETE_REQUEST.copy(method = Method.PATCH, body = REQUEST_BODY),
            getCompleteRequestBuilder(netMock.baseUrl).patch(REQUEST_BODY.toRequestBody()).build(),
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

        val response = sut.newCall(getRequestWithMissingFields(netMock.baseUrl)).execute()

        assertEquals(listOf(EXPECTED_MISSING_FIELDS_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    @Test
    fun `EXPECT default response WHEN request is not matching`() {
        netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)

        val response = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()

        assertTrue(netMock.interceptedRequests.isEmpty())
        assertEquals(400, response.code)
        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)),
            netMock.allowedMocks
        )
        assertEquals("Request not mocked:\nGET /somePath?1=2&3=4 HTTP/1.1", response.body!!.string())
    }

    @Test
    fun `EXPECT overridden default response WHEN request is not matching and default response is overridden`() {
        netMock.addMock(EXPECTED_NOT_MATCHING_REQUEST, EXPECTED_RESPONSE)
        netMock.setDefaultResponse(DEFAULT_RESPONSE)

        val response = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()

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

    @After
    fun tearDown() {
        netMock.shutDown()
    }

    private companion object {
        const val REQUEST_BODY = "requestBody"
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            path = "/somePath",
            method = Method.GET,
            containsHeaders = mapOf("a" to "b", "c" to "d"),
            params = mapOf("1" to "2", "3" to "4")
        )
        val EXPECTED_MISSING_FIELDS_REQUEST = NetMockRequest(method = Method.GET)
        val EXPECTED_NOT_MATCHING_REQUEST = EXPECTED_COMPLETE_REQUEST.copy(
            containsHeaders = mapOf("a" to "b", "c" to "d", "e" to "f")
        )
        val EXPECTED_RESPONSE = NetMockResponse(code = 200, containsHeaders = mapOf("x" to "y"), body = "data")
        val DEFAULT_RESPONSE = NetMockResponse(code = 201, containsHeaders = mapOf("a" to "b"), body = "default")
        private fun getCompleteRequest(baseUrl: String): Request {
            return getCompleteRequestBuilder(baseUrl).get().build()
        }

        private fun getRequestWithMissingFields(baseUrl: String): Request {
            return Request.Builder()
                .get()
                .url(baseUrl)
                .build()
        }

        private fun getCompleteRequestBuilder(baseUrl: String): Request.Builder {
            return Request.Builder()
                .headers(Headers.headersOf("a", "b", "c", "d"))
                .url("${baseUrl}somePath?1=2&3=4")
        }
    }
}