package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.*
import com.denisbrandi.netmock.resources.readFromJvmResources
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.*
import org.junit.Assert.*

class NetMockServerTest {
    @get:Rule
    val netMock = NetMockServerRule()

    private val sut = OkHttpClient.Builder()
        .addInterceptor(netMock.interceptor)
        .build()

    @Test
    fun `EXPECT mapped response WHEN directed to localhost`() {
        val sut = OkHttpClient.Builder().build()
        val expectedRequest =
            EXPECTED_COMPLETE_REQUEST.copy(requestUrl = "${netMock.baseUrl}somePath?1=2&3=4")
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response = sut.newCall(getCompleteRequest(netMock.baseUrl)).execute()

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
    fun `EXPECT mapped response WHEN using custom matcher`() {
        netMock.addMockWithCustomMatcher(
            requestMatcher = { it.requestUrl == EXPECTED_COMPLETE_REQUEST.requestUrl },
            response = EXPECTED_RESPONSE
        )

        val response = sut.newCall(getCompleteRequest(BASE_URL)).execute()

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
    fun `EXPECT mapped responses WHEN retaining mocks`() {
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, EXPECTED_RESPONSE, retainMock = true)

        val response1 = sut.newCall(getCompleteRequest(BASE_URL)).execute()
        val response2 = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertEquals(
            listOf(EXPECTED_COMPLETE_REQUEST, EXPECTED_COMPLETE_REQUEST),
            netMock.interceptedRequests
        )
        assertValidResponse(EXPECTED_RESPONSE, response1)
        assertValidResponse(EXPECTED_RESPONSE, response2)
        assertEquals(
            listOf(
                NetMockRequestResponse(
                    EXPECTED_COMPLETE_REQUEST,
                    EXPECTED_RESPONSE,
                    true
                )
            ), netMock.allowedMocks
        )
    }

    @Test
    fun `EXPECT mapped responses WHEN retaining mocks and using custom matcher`() {
        netMock.addMockWithCustomMatcher(
            requestMatcher = { it.requestUrl == EXPECTED_COMPLETE_REQUEST.requestUrl },
            response = EXPECTED_RESPONSE,
            retainMock = true
        )

        val response1 = sut.newCall(getCompleteRequest(BASE_URL)).execute()
        val response2 = sut.newCall(getCompleteRequest(BASE_URL)).execute()

        assertEquals(2, netMock.interceptedRequests.size)
        assertInterceptedRequestWithCustomMatcher(
            EXPECTED_COMPLETE_REQUEST,
            netMock.interceptedRequests.first()
        )
        assertInterceptedRequestWithCustomMatcher(
            EXPECTED_COMPLETE_REQUEST,
            netMock.interceptedRequests[1]
        )
        assertValidResponse(EXPECTED_RESPONSE, response1)
        assertValidResponse(EXPECTED_RESPONSE, response2)
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
        testResponseForMethod(Method.Get, null, EXPECTED_RESPONSE)
    }

    @Test
    fun `EXPECT valid response for HEAD`() {
        // body is empty in head responses
        testResponseForMethod(Method.Head, null, EXPECTED_RESPONSE.copy(body = ""))
    }

    @Test
    fun `EXPECT valid response for POST`() {
        testResponseForMethod(Method.Post, REQUEST_BODY, EXPECTED_RESPONSE)
    }

    @Test
    fun `EXPECT valid response for PUT`() {
        testResponseForMethod(Method.Put, REQUEST_BODY, EXPECTED_RESPONSE)
    }

    @Test
    fun `EXPECT valid response for DELETE`() {
        testResponseForMethod(Method.Delete, REQUEST_BODY, EXPECTED_RESPONSE)
    }

    @Test
    fun `EXPECT valid response for PATCH`() {
        testResponseForMethod(Method.Patch, REQUEST_BODY, EXPECTED_RESPONSE)
    }

    @Test
    fun `EXPECT valid response for custom method`() {
        testResponseForMethod(Method.Custom("CUSTOM"), REQUEST_BODY, EXPECTED_RESPONSE)
    }

    private fun testResponseForMethod(
        method: Method,
        body: String?,
        expectedResponse: NetMockResponse
    ) {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = method, body = body.orEmpty())
        netMock.addMock(expectedRequest, expectedResponse)
        val request = getCompleteRequestBuilder(BASE_URL).method(
            method.name,
            body?.toRequestBody()
        ).build()

        val response = sut.newCall(request).execute()

        assertEquals(listOf(expectedRequest), netMock.interceptedRequests)
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

    @Test
    fun `EXPECT mapped response WHEN request is form data`() {
        netMock.addMock(EXPECTED_FORM_DATA_REQUEST, EXPECTED_RESPONSE)

        val response = sut.newCall(postFormDataRequest()).execute()

        assertEquals(listOf(EXPECTED_FORM_DATA_REQUEST), netMock.interceptedRequests)
        assertValidResponse(EXPECTED_RESPONSE, response)
        assertTrue(netMock.allowedMocks.isEmpty())
    }

    private fun assertValidResponse(expectedResponse: NetMockResponse, actualResponse: Response) {
        assertEquals(expectedResponse.code, actualResponse.code)
        expectedResponse.mandatoryHeaders.forEach {
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
            mandatoryHeaders = mapOf("a" to "b", "c" to "d")
        )
        val EXPECTED_MISSING_FIELDS_REQUEST =
            NetMockRequest(requestUrl = "https://google.com/", method = Method.Get)
        val EXPECTED_NOT_MATCHING_REQUEST = EXPECTED_COMPLETE_REQUEST.copy(
            mandatoryHeaders = mapOf("a" to "b", "c" to "d", "e" to "f")
        )
        val EXPECTED_FORM_DATA_REQUEST =
            NetMockRequest(
                requestUrl = "https://google.com/",
                method = Method.Post,
                mandatoryHeaders = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
                body = "form_key_1=form_value_1&form_key_2=form_value_2&form_key_2=form+value+3"
            )
        val EXPECTED_RESPONSE = NetMockResponse(
            code = 200,
            mandatoryHeaders = mapOf("x" to "y"),
            body = readFromJvmResources("response_body.json")
        )
        val DEFAULT_RESPONSE =
            NetMockResponse(code = 201, mandatoryHeaders = mapOf("a" to "b"), body = "default")

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

        private fun postFormDataRequest(): Request {
            return Request.Builder()
                .post(
                    FormBody.Builder()
                        .add("form_key_1", "form_value_1")
                        .add("form_key_2", "form_value_2")
                        .addEncoded("form_key_2", "form+value+3")
                        .build()
                )
                .url(BASE_URL)
                .headers(Headers.headersOf("Content-Type", "application/x-www-form-urlencoded"))
                .build()
        }
    }
}
