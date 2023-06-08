package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.NetMockRequest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RequestPathMatcherTest {
    private val sut = RequestPathMatcher

    @JsName("true_noPath")
    @Test
    fun `EXPECT true WHEN intercepted path is null and mocked request path is empty`() {
        assertTrue(sut.isMatchingPathAndParams(null, NO_PATH_REQUEST))
    }

    @JsName("true_emptyPath")
    @Test
    fun `EXPECT true WHEN intercepted path is empty and mocked request path is empty`() {
        assertTrue(sut.isMatchingPathAndParams("", NO_PATH_REQUEST))
    }

    @JsName("true_samePath")
    @Test
    fun `EXPECT true WHEN intercepted path matches`() {
        assertTrue(sut.isMatchingPathAndParams("/somePath", ONLY_PATH_REQUEST))
    }

    @JsName("true_samePathAndParams")
    @Test
    fun `EXPECT true WHEN intercepted path and queries match`() {
        val result = sut.isMatchingPathAndParams(
            "/somePath?param1=value1&param2=value2",
            PATH_AND_PARAMS_REQUEST
        )

        assertTrue(result)
    }

    @JsName("false_differentPath")
    @Test
    fun `EXPECT false WHEN intercepted path is different`() {
        val result = sut.isMatchingPathAndParams(
            "/somePath2?param1=value1&param2=value2",
            PATH_AND_PARAMS_REQUEST
        )

        assertFalse(result)
    }

    @JsName("false_differentParams")
    @Test
    fun `EXPECT false WHEN intercepted params are different`() {
        val result = sut.isMatchingPathAndParams(
            "/somePath?param1=value1&param2=differentValue",
            PATH_AND_PARAMS_REQUEST
        )

        assertFalse(result)
    }

    @JsName("true_paramsInPath")
    @Test
    fun `EXPECT true WHEN intercepted mocked path has also params`() {
        val result = sut.isMatchingPathAndParams(
            "/somePath?param1=value1&param2=value2",
            PATH_WITH_PARAMS_REQUEST
        )

        assertTrue(result)
    }

    @JsName("true_paramsInPathAnd?")
    @Test
    fun `EXPECT true WHEN intercepted mocked path has also params and there is a question mark in the params`() {
        val result = sut.isMatchingPathAndParams(
            "/somePath?param1=value?&param2=value?",
            PATH_WITH_PARAMS_AND_QM_REQUEST
        )

        assertTrue(result)
    }

    private companion object {
        val NO_PATH_REQUEST = NetMockRequest(path = "")
        val ONLY_PATH_REQUEST = NetMockRequest(path = "/somePath")
        val PATH_AND_PARAMS_REQUEST = NetMockRequest(
            path = "/somePath",
            params = mapOf("param1" to "value1", "param2" to "value2")
        )
        val PATH_WITH_PARAMS_REQUEST = NetMockRequest(
            path = "/somePath?param1=value1",
            params = mapOf("param2" to "value2")
        )
        val PATH_WITH_PARAMS_AND_QM_REQUEST = NetMockRequest(
            path = "/somePath?param1=value?",
            params = mapOf("param2" to "value?")
        )
    }
}
