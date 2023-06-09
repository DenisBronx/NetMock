package com.denisbrandi.netmock

import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

class NetMockTest {

    private val sut = SpyNetMock()

    @JsName("call_allFields")
    @Test
    fun `EXPECT interface method properly called with all fields`() {
        sut.addMock(
            request = {
                requestUrl = "http://google.com/somePath"
                method = Method.Post
                containsHeaders = mapOf("a" to "b", "c" to "d")
                params = mapOf("1" to "2", "3" to "4")
                body = "body"
            },
            response = {
                code = 200
                containsHeaders = mapOf("x" to "y")
                body = "responseBody"
            }
        )

        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)),
            sut.allowedMocks
        )
    }

    @JsName("call_withBuilders")
    @Test
    fun `EXPECT interface method properly called with all fields WHEN passing builders`() {
        sut.addMock(
            request = {
                fromBuilder(EXPECTED_REQUEST_BUILDER)
            },
            response = {
                fromBuilder(EXPECTED_RESPONSE_BUILDER)
            }
        )

        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)),
            sut.allowedMocks
        )
    }

    @JsName("call_withRequest")
    @Test
    fun `EXPECT interface method properly called with all fields WHEN passing requests and responses`() {
        sut.addMock(
            request = {
                fromRequest(EXPECTED_REQUEST)
            },
            response = {
                fromResponse(EXPECTED_RESPONSE)
            }
        )

        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)),
            sut.allowedMocks
        )
    }

    @JsName("call_noFields")
    @Test
    fun `EXPECT interface method properly called with no fields`() {
        sut.addMock(request = {}, response = {})

        assertEquals(
            listOf(NetMockRequestResponse(NetMockRequest(), NetMockResponse())),
            sut.allowedMocks
        )
    }

    private class SpyNetMock : NetMock {
        override val interceptedRequests: List<NetMockRequest> = emptyList()
        override val allowedMocks = mutableListOf<NetMockRequestResponse>()
        override var defaultResponse: NetMockResponse? = null
        override fun addMock(request: NetMockRequest, response: NetMockResponse) {
            allowedMocks.add(NetMockRequestResponse(request, response))
        }
    }

    private companion object {
        val EXPECTED_REQUEST = NetMockRequest(
            requestUrl = "http://google.com/somePath",
            method = Method.Post,
            containsHeaders = mapOf("a" to "b", "c" to "d"),
            params = mapOf("1" to "2", "3" to "4"),
            body = "body"
        )
        val EXPECTED_REQUEST_BUILDER = NetMockRequestBuilder().apply {
            requestUrl = "http://google.com/somePath"
            method = Method.Post
            containsHeaders = mapOf("a" to "b", "c" to "d")
            params = mapOf("1" to "2", "3" to "4")
            body = "body"
        }
        val EXPECTED_RESPONSE = NetMockResponse(
            code = 200,
            containsHeaders = mapOf("x" to "y"),
            body = "responseBody"
        )
        val EXPECTED_RESPONSE_BUILDER = NetMockResponseBuilder().apply {
            code = 200
            containsHeaders = mapOf("x" to "y")
            body = "responseBody"
        }
    }
}
