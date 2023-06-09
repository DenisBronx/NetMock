package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.InterceptedRequest
import com.denisbrandi.netmock.Method
import com.denisbrandi.netmock.NetMockRequest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultRequestMatcherTest {
    private val sut = DefaultRequestMatcher

    @JsName("false_badUrl")
    @Test
    fun `EXPECT false WHEN url doesn't match`() {
        val notMatchingRequest = VALID_INTERCEPTED_REQUEST.copy(requestUrl = null)

        val result = sut.isMatchingTheRequest(notMatchingRequest, EXPECTED_REQUEST)

        assertFalse(result)
    }

    @JsName("false_badMethod")
    @Test
    fun `EXPECT false WHEN method doesn't match`() {
        val notMatchingRequest = VALID_INTERCEPTED_REQUEST.copy(method = null)

        val result = sut.isMatchingTheRequest(notMatchingRequest, EXPECTED_REQUEST)

        assertFalse(result)
    }

    @JsName("false_badHeaders")
    @Test
    fun `EXPECT false WHEN headers don't match`() {
        val notMatchingRequest = VALID_INTERCEPTED_REQUEST.copy(headers = null)

        val result = sut.isMatchingTheRequest(notMatchingRequest, EXPECTED_REQUEST)

        assertFalse(result)
    }

    @JsName("false_badBody")
    @Test
    fun `EXPECT false WHEN body doesn't match`() {
        val notMatchingRequest = VALID_INTERCEPTED_REQUEST.copy(body = null)

        val result = sut.isMatchingTheRequest(notMatchingRequest, EXPECTED_REQUEST)

        assertFalse(result)
    }

    @JsName("true_matchingRequests")
    @Test
    fun `EXPECT true WHEN requests are matching`() {
        val result = sut.isMatchingTheRequest(VALID_INTERCEPTED_REQUEST, EXPECTED_REQUEST)

        assertTrue(result)
    }

    private companion object {
        val VALID_INTERCEPTED_REQUEST = InterceptedRequest(
            requestUrl = "https://google.com/somePath?paramKey1=paramValue1&paramKey2=paramValue2",
            method = "GET",
            headers = mapOf("headerKey1" to "headerValue1", "headerKey2" to "headerValue2"),
            body = "body"
        )
        val EXPECTED_REQUEST = NetMockRequest(
            requestUrl = "https://google.com/somePath?paramKey1=paramValue1&paramKey2=paramValue2",
            method = Method.Get,
            mandatoryHeaders = mapOf("headerKey1" to "headerValue1", "headerKey2" to "headerValue2"),
            body = "body"
        )
    }
}
