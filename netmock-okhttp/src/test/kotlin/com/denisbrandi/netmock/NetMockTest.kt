package com.denisbrandi.netmock

import org.junit.Assert.assertEquals
import org.junit.Test

class NetMockTest {

    private val sut = SpyNetMock()

    @Test
    fun `EXPECT interface method properly called with all fields`() {
        sut.addMock(
            request = {
                path = "/somePath"
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
            listOf(
                NetMockRequestResponse(
                    NetMockRequest(
                        path = "/somePath",
                        method = Method.Post,
                        containsHeaders = mapOf("a" to "b", "c" to "d"),
                        params = mapOf("1" to "2", "3" to "4"),
                        body = "body"
                    ),
                    NetMockResponse(
                        code = 200,
                        containsHeaders = mapOf("x" to "y"),
                        body = "responseBody"
                    )
                )
            ),
            sut.allowedMocks
        )
    }

    @Test
    fun `EXPECT interface method properly called with no fields`() {
        sut.addMock(
            request = {},
            response = {}
        )

        val expected = listOf(NetMockRequestResponse(NetMockRequest(), NetMockResponse()))
        assertEquals(expected, sut.allowedMocks)
    }

    private class SpyNetMock : NetMock {
        override val baseUrl: String
            get() = TODO("Not yet implemented")
        override val interceptedRequests: List<NetMockRequest>
            get() = TODO("Not yet implemented")
        override val allowedMocks = mutableListOf<NetMockRequestResponse>()

        override fun start() {
            TODO("Not yet implemented")
        }

        override fun addMock(request: NetMockRequest, response: NetMockResponse) {
            allowedMocks.add(NetMockRequestResponse(request, response))
        }

        override fun setDefaultResponse(netMockResponse: NetMockResponse) {
            TODO("Not yet implemented")
        }

        override fun shutDown() {
            TODO("Not yet implemented")
        }

    }
}