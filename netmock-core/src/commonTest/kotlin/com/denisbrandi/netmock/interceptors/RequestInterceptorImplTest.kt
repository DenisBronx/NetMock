package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockRequestResponse
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.mappers.ResponseMapper
import com.denisbrandi.netmock.matchers.RequestMatcher
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RequestInterceptorImplTest {
    private val fakeRequestMatcher = FakeRequestMatcher()
    private val fakeResponseMapper = FakeResponseMapper()
    private val sut = RequestInterceptorImpl(fakeRequestMatcher, fakeResponseMapper)

    @JsName("default_noMocks")
    @Test
    fun `EXPECT default response WHEN no allowed mocks`() {
        fakeRequestMatcher.isMatching = false
        fakeResponseMapper.map[makeDefaultResponse(emptyList(), emptyList())] = DEFAULT_MAPPED_RESPONSE

        val result = sut.intercept(INTERCEPTED_REQUEST, HEADERS, INTERCEPTED_REQUEST_BODY)

        assertEquals(DEFAULT_MAPPED_RESPONSE, result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("default_noMatch")
    @Test
    fun `EXPECT default response WHEN request does not match and there is no default response`() {
        fakeRequestMatcher.isMatching = false
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)
        fakeResponseMapper.map[makeDefaultResponse(listOf(ALLOWED_MOCK), emptyList())] = DEFAULT_MAPPED_RESPONSE

        val result = sut.intercept(INTERCEPTED_REQUEST, HEADERS, INTERCEPTED_REQUEST_BODY)

        assertEquals(DEFAULT_MAPPED_RESPONSE, result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("default_defaultResponse")
    @Test
    fun `EXPECT default response WHEN request does not match and there is default response`() {
        fakeRequestMatcher.isMatching = false
        sut.defaultResponse = EXPECTED_RESPONSE
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST, HEADERS, INTERCEPTED_REQUEST_BODY)

        assertEquals(MAPPED_RESPONSE, result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("mappedResponse_match")
    @Test
    fun `EXPECT mapped response WHEN request matches`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST, HEADERS, INTERCEPTED_REQUEST_BODY)

        assertEquals(MAPPED_RESPONSE, result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    @JsName("default_multipleCalls")
    @Test
    fun `EXPECT default response WHEN request was mocked only once`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)
        fakeResponseMapper.map[
            makeDefaultResponse(emptyList(), listOf(EXPECTED_REQUEST))
        ] = DEFAULT_MAPPED_RESPONSE

        sut.intercept(INTERCEPTED_REQUEST, HEADERS, INTERCEPTED_REQUEST_BODY)
        val result = sut.intercept(INTERCEPTED_REQUEST, HEADERS, INTERCEPTED_REQUEST_BODY)

        assertEquals(DEFAULT_MAPPED_RESPONSE, result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    private class FakeRequestMatcher : RequestMatcher<TestRequest> {
        var isMatching = false
        override fun isMatchingTheRequest(
            recordedRequest: TestRequest,
            recordedRequestBody: String,
            expectedRequest: NetMockRequest
        ): Boolean {
            return if (
                INTERCEPTED_REQUEST === recordedRequest &&
                INTERCEPTED_REQUEST_BODY == recordedRequestBody &&
                EXPECTED_REQUEST === expectedRequest
            ) {
                isMatching
            } else {
                throw ASSERTION_ERROR
            }
        }
    }

    private class FakeResponseMapper : ResponseMapper<TestResponse> {
        val map = mutableMapOf(EXPECTED_RESPONSE to MAPPED_RESPONSE)

        override fun mapResponse(netMockResponse: NetMockResponse): TestResponse {
            return map[netMockResponse] ?: throw ASSERTION_ERROR
        }
    }

    private data class TestRequest(val data: String)
    private data class TestResponse(val data: String)

    private companion object {
        val HEADERS = mapOf("a" to "b", "c" to "d")
        val ASSERTION_ERROR = AssertionError("Invalid invocation")
        val INTERCEPTED_REQUEST = TestRequest("request")
        const val INTERCEPTED_REQUEST_BODY = "requestBody"
        val EXPECTED_REQUEST = NetMockRequest(body = INTERCEPTED_REQUEST_BODY)
        val EXPECTED_RESPONSE = NetMockResponse(body = "data")
        val MAPPED_RESPONSE = TestResponse("response")
        val ALLOWED_MOCK = NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)
        fun makeDefaultResponse(
            allowedMocks: List<NetMockRequestResponse> = emptyList(),
            interceptedRequests: List<NetMockRequest> = emptyList()
        ): NetMockResponse {
            return NetMockResponse(
                code = 400,
                body = "\n----\nRequest not mocked:\n${INTERCEPTED_REQUEST}\nWith headers:\n${HEADERS}With body:\n$INTERCEPTED_REQUEST_BODY" +
                    "\n\nThe following requests and responses were expected:\n$allowedMocks" +
                    "\n\nThe following requests have been successfully mocked:\n$interceptedRequests" +
                    "\n----"
            )
        }

        val DEFAULT_MAPPED_RESPONSE = TestResponse("default")
    }
}
