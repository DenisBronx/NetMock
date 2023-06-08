package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockRequestResponse
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.mappers.ResponseMapper
import com.denisbrandi.netmock.matchers.RequestMatcher
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RequestInterceptorImplTest {
    private val fakeRequestMatcher = FakeRequestMatcher()
    private val fakeResponseMapper = FakeResponseMapper()
    private val sut = RequestInterceptorImpl(fakeRequestMatcher, fakeResponseMapper)

    @JsName("null_noMocks")
    @Test
    fun `EXPECT null WHEN no allowed mocks`() {
        fakeRequestMatcher.isMatching = false

        val result = sut.intercept(INTERCEPTED_REQUEST, INTERCEPTED_REQUEST_BODY)

        assertNull(result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("null_noMatch")
    @Test
    fun `EXPECT null WHEN request does not match and there is no default response`() {
        fakeRequestMatcher.isMatching = false
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST, INTERCEPTED_REQUEST_BODY)

        assertNull(result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("null_defaultResponse")
    @Test
    fun `EXPECT default response WHEN request does not match and there is default response`() {
        fakeRequestMatcher.isMatching = false
        sut.defaultResponse = EXPECTED_RESPONSE
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST, INTERCEPTED_REQUEST_BODY)

        assertEquals(MAPPED_RESPONSE, result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("mappedResponse_match")
    @Test
    fun `EXPECT mapped response WHEN request matches`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST, INTERCEPTED_REQUEST_BODY)

        assertEquals(MAPPED_RESPONSE, result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    @JsName("null_multipleCalls")
    @Test
    fun `EXPECT null WHEN request was mocked only once`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        sut.intercept(INTERCEPTED_REQUEST, INTERCEPTED_REQUEST_BODY)
        val result = sut.intercept(INTERCEPTED_REQUEST, INTERCEPTED_REQUEST_BODY)

        assertNull(result)
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
        override fun mapResponse(netMockResponse: NetMockResponse): TestResponse {
            return if (EXPECTED_RESPONSE === netMockResponse) {
                MAPPED_RESPONSE
            } else {
                throw ASSERTION_ERROR
            }
        }
    }

    private data class TestRequest(val data: String)
    private data class TestResponse(val data: String)

    private companion object {
        val ASSERTION_ERROR = AssertionError("Invalid invocation")
        val INTERCEPTED_REQUEST = TestRequest("request")
        const val INTERCEPTED_REQUEST_BODY = "requestBody"
        val EXPECTED_REQUEST = NetMockRequest(body = INTERCEPTED_REQUEST_BODY)
        val EXPECTED_RESPONSE = NetMockResponse(body = "data")
        val MAPPED_RESPONSE = TestResponse("response")
        val ALLOWED_MOCK = NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)
    }
}