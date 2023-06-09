package com.denisbrandi.netmock.matchers

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RequestUrlMatcherTest {
    private val sut = RequestUrlMatcher

    @JsName("true_noPath")
    @Test
    fun `EXPECT true WHEN intercepted path is null and mocked request path is empty`() {
        assertTrue(sut.isMatchingUrl(null, ""))
    }

    @JsName("true_emptyPath")
    @Test
    fun `EXPECT true WHEN intercepted path is empty and mocked request path is empty`() {
        assertTrue(sut.isMatchingUrl("", ""))
    }

    @JsName("true_samePath")
    @Test
    fun `EXPECT true WHEN intercepted path matches`() {
        assertTrue(sut.isMatchingUrl(VALID_REQUEST_URL, VALID_REQUEST_URL))
    }

    @JsName("false_differentPath")
    @Test
    fun `EXPECT false WHEN intercepted path is different`() {
        val result = sut.isMatchingUrl(
            "http://google.com/somePath2?param1=value1&param2=value2",
            VALID_REQUEST_URL
        )

        assertFalse(result)
    }

    private companion object {
        const val VALID_REQUEST_URL = "http://google.com/somePath?param1=value1"
    }
}
