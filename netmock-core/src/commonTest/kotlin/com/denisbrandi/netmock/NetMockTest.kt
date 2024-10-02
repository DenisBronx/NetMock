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
                requestUrl = "http://google.com/somePath?1=2&3=4"
                method = Method.Post
                mandatoryHeaders = mapOf("a" to "b", "c" to "d")
                body = "body"
            },
            response = {
                code = 200
                mandatoryHeaders = mapOf("x" to "y")
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
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)),
            sut.allowedMocks
        )
    }

    @JsName("call_withRequestAndBuilder")
    @Test
    fun `EXPECT interface method properly called with all fields WHEN passing requests and response builder`() {
        sut.addMock(
            request = EXPECTED_REQUEST,
            response = {
                fromBuilder(EXPECTED_RESPONSE_BUILDER)
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
            requestUrl = "http://google.com/somePath?1=2&3=4",
            method = Method.Post,
            mandatoryHeaders = mapOf("a" to "b", "c" to "d"),
            body = "body"
        )
        val EXPECTED_REQUEST_BUILDER = NetMockRequestBuilder().apply {
            requestUrl = "http://google.com/somePath?1=2&3=4"
            method = Method.Post
            mandatoryHeaders = mapOf("a" to "b", "c" to "d")
            body = "body"
        }
        val EXPECTED_RESPONSE = NetMockResponse(
            code = 200,
            mandatoryHeaders = mapOf("x" to "y"),
            body = "responseBody"
        )
        val EXPECTED_RESPONSE_BUILDER = NetMockResponseBuilder().apply {
            code = 200
            mandatoryHeaders = mapOf("x" to "y")
            body = "responseBody"
        }
    }
}
