package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.*
import com.denisbrandi.netmock.matchers.RequestMatcher
import kotlin.js.JsName
import kotlin.test.*

class DefaultInterceptorTest {
    private val fakeRequestMatcher = FakeRequestMatcher()
    private val sut = DefaultInterceptor(fakeRequestMatcher)

    @JsName("default_noMocks")
    @Test
    fun `EXPECT default response WHEN no allowed mocks`() {
        fakeRequestMatcher.isMatching = false

        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(makeDefaultResponse(emptyList(), emptyList()), result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("default_noMatch")
    @Test
    fun `EXPECT default response WHEN request does not match and there is no default response`() {
        fakeRequestMatcher.isMatching = false
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(makeDefaultResponse(listOf(ALLOWED_MOCK), emptyList()), result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("default_defaultResponse")
    @Test
    fun `EXPECT default response WHEN request does not match and there is default response`() {
        fakeRequestMatcher.isMatching = false
        sut.defaultResponse = EXPECTED_RESPONSE
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(EXPECTED_RESPONSE, result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertTrue(sut.interceptedRequests.isEmpty())
    }

    @JsName("mappedResponse_match")
    @Test
    fun `EXPECT mapped response WHEN request matches`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(EXPECTED_RESPONSE, result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    @JsName("default_multipleCalls")
    @Test
    fun `EXPECT default response WHEN request was mocked only once`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        sut.intercept(INTERCEPTED_REQUEST)
        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(makeDefaultResponse(emptyList(), listOf(EXPECTED_REQUEST)), result)
        assertTrue(sut.allowedMocks.isEmpty())
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    @JsName("mappedResponse_matchCustomMock")
    @Test
    fun `EXPECT mapped response WHEN request matches the custom matcher`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)
        val expectedCustomResponse = NetMockResponse(code = 200)
        sut.addMockWithCustomMatcher(
            requestMatcher = { interceptedRequest ->
                interceptedRequest.requestUrl.contains("request")
            },
            response = expectedCustomResponse
        )

        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(expectedCustomResponse, result)
        assertEquals(listOf(ALLOWED_MOCK), sut.allowedMocks)
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    @JsName("mappedResponse_matchCustomMock")
    @Test
    fun `EXPECT mapped response WHEN request matches only the mocks`() {
        fakeRequestMatcher.isMatching = true
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)
        val expectedCustomResponse = NetMockResponse(code = 200)
        sut.addMockWithCustomMatcher(
            requestMatcher = { interceptedRequest ->
                interceptedRequest.requestUrl.contains("google.com")
            },
            response = expectedCustomResponse
        )

        val result = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(EXPECTED_RESPONSE, result)
        assertEquals(emptyList(), sut.allowedMocks)
        assertEquals(listOf(EXPECTED_REQUEST), sut.interceptedRequests)
    }

    @JsName("default_multipleCallsOnCustomMatcher")
    @Test
    fun `EXPECT mapped responses WHEN requests match the custom matchers multiple times`() {
        val expectedCustomResponse = NetMockResponse(code = 200, body = "response")
        sut.addMockWithCustomMatcher(
            requestMatcher = { interceptedRequest ->
                interceptedRequest.requestUrl.contains("customRequest")
            },
            response = expectedCustomResponse
        )

        val result1 = sut.intercept(INTERCEPTED_REQUEST.copy(requestUrl = "customRequest"))
        val result2 = sut.intercept(INTERCEPTED_REQUEST)

        assertEquals(expectedCustomResponse, result1)
        assertEquals(
            makeDefaultResponse(
                emptyList(),
                listOf(EXPECTED_REQUEST.copy(requestUrl = "customRequest"))
            ), result2
        )
        assertEquals(
            listOf(EXPECTED_REQUEST.copy(requestUrl = "customRequest")),
            sut.interceptedRequests
        )
    }

    private class FakeRequestMatcher : RequestMatcher {
        var isMatching = false
        override fun isMatchingTheRequest(
            interceptedRequest: InterceptedRequest,
            expectedRequest: NetMockRequest
        ): Boolean {
            return if (
                INTERCEPTED_REQUEST === interceptedRequest &&
                EXPECTED_REQUEST === expectedRequest
            ) {
                isMatching
            } else {
                throw ASSERTION_ERROR
            }
        }
    }

    private companion object {
        val HEADERS = mapOf("a" to "b", "c" to "d")
        val ASSERTION_ERROR = AssertionError("Invalid invocation")
        const val INTERCEPTED_REQUEST_BODY = "requestBody"
        val INTERCEPTED_REQUEST =
            InterceptedRequest(
                requestUrl = "requestUrl",
                method = "method",
                headers = HEADERS,
                body = INTERCEPTED_REQUEST_BODY
            )
        val EXPECTED_REQUEST = NetMockRequest(
            requestUrl = "requestUrl",
            method = Method.Custom("method"),
            mandatoryHeaders = HEADERS,
            body = INTERCEPTED_REQUEST_BODY
        )
        val EXPECTED_RESPONSE = NetMockResponse(body = "data")
        val ALLOWED_MOCK = NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)
        fun makeDefaultResponse(
            allowedMocks: List<NetMockRequestResponse> = emptyList(),
            interceptedRequests: List<NetMockRequest> = emptyList()
        ): NetMockResponse {
            return NetMockResponse(
                code = 400,
                body = "\n----\nRequest not mocked:\n${INTERCEPTED_REQUEST.method} ${INTERCEPTED_REQUEST.requestUrl}\nWith headers:\n${HEADERS}\nWith body:\n$INTERCEPTED_REQUEST_BODY" +
                    "\n\nThe following requests and responses were expected:\n$allowedMocks" +
                    "\n\nThe following requests have been successfully mocked:\n$interceptedRequests" +
                    "\n----"
            )
        }
    }
}
