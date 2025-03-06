package com.denisbrandi.netmock

import kotlin.js.JsName
import kotlin.test.*

class NetMockTest {

    private val sut = SpyNetMock()

    @JsName("addMock_allFields")
    @Test
    fun `EXPECT addMock properly called with all fields`() {
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
            },
            retainMock = true
        )

        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE, true)),
            sut.allowedMocks
        )
    }

    @JsName("addMock_withBuilders")
    @Test
    fun `EXPECT addMock properly called with all fields WHEN passing builders`() {
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

    @JsName("addMock_withRequest")
    @Test
    fun `EXPECT addMock properly called with all fields WHEN passing requests and responses`() {
        sut.addMock(EXPECTED_REQUEST, EXPECTED_RESPONSE)

        assertEquals(
            listOf(NetMockRequestResponse(EXPECTED_REQUEST, EXPECTED_RESPONSE)),
            sut.allowedMocks
        )
    }

    @JsName("addMock_withRequestAndBuilder")
    @Test
    fun `EXPECT addMock properly called with all fields WHEN passing requests and response builder`() {
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

    @JsName("addMock_noFields")
    @Test
    fun `EXPECT addMock properly called with no fields`() {
        sut.addMock(request = {}, response = {})

        assertEquals(
            listOf(NetMockRequestResponse(NetMockRequest(), NetMockResponse())),
            sut.allowedMocks
        )
    }

    @JsName("addMockWithCustomMatcher_allFields")
    @Test
    fun `EXPECT addMockWithCustomMatcher properly called with all fields`() {
        sut.addMockWithCustomMatcher(
            requestMatcher = CUSTOM_REQUEST_MATCHER,
            response = {
                code = 200
                mandatoryHeaders = mapOf("x" to "y")
                body = "responseBody"
            },
            retainMock = true
        )

        assertEquals(
            listOf(SpyNetMock.CustomInterceptor(CUSTOM_REQUEST_MATCHER, EXPECTED_RESPONSE, true)),
            sut.customInterceptors
        )
    }

    @JsName("addMockWithCustomMatcher_withBuilder")
    @Test
    fun `EXPECT addMockWithCustomMatcher properly called with all fields WHEN passing builders`() {
        sut.addMockWithCustomMatcher(
            requestMatcher = CUSTOM_REQUEST_MATCHER,
            response = {
                fromBuilder(EXPECTED_RESPONSE_BUILDER)
            }
        )

        assertEquals(
            listOf(SpyNetMock.CustomInterceptor(CUSTOM_REQUEST_MATCHER, EXPECTED_RESPONSE)),
            sut.customInterceptors
        )
    }

    @JsName("addMockWithCustomMatcher_withRequest")
    @Test
    fun `EXPECT addMockWithCustomMatcher properly called with all fields WHEN passing requests and responses`() {
        sut.addMockWithCustomMatcher(
            requestMatcher = CUSTOM_REQUEST_MATCHER,
            response = EXPECTED_RESPONSE
        )

        assertEquals(
            listOf(SpyNetMock.CustomInterceptor(CUSTOM_REQUEST_MATCHER, EXPECTED_RESPONSE)),
            sut.customInterceptors
        )
    }

    @JsName("addMockWithCustomMatcher_noResponseField")
    @Test
    fun `EXPECT addMockWithCustomMatcher properly called with no response field`() {
        sut.addMockWithCustomMatcher(
            requestMatcher = CUSTOM_REQUEST_MATCHER,
            response = {}
        )

        assertEquals(
            listOf(SpyNetMock.CustomInterceptor(CUSTOM_REQUEST_MATCHER, NetMockResponse())),
            sut.customInterceptors
        )
    }

    private class SpyNetMock : NetMock {
        override val interceptedRequests: List<NetMockRequest> = emptyList()
        override val allowedMocks = mutableListOf<NetMockRequestResponse>()
        override var defaultResponse: NetMockResponse? = null
        val customInterceptors = mutableListOf<CustomInterceptor>()
        override fun addMock(
            request: NetMockRequest,
            response: NetMockResponse,
            retainMock: Boolean
        ) {
            allowedMocks.add(NetMockRequestResponse(request, response, retainMock))
        }

        override fun addMockWithCustomMatcher(
            requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
            response: NetMockResponse,
            retainMock: Boolean
        ) {
            customInterceptors.add(CustomInterceptor(requestMatcher, response, retainMock))
        }

        data class CustomInterceptor(
            val requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
            val response: NetMockResponse,
            val retainMock: Boolean = false
        )
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
        val CUSTOM_REQUEST_MATCHER: (interceptedRequest: NetMockRequest) -> Boolean =
            { interceptedRequest ->
                interceptedRequest == EXPECTED_REQUEST
            }
    }
}
