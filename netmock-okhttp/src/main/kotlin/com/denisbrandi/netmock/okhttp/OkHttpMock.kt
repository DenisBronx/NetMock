package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.*
import okhttp3.mockwebserver.MockWebServer

class OkHttpMock : NetMock {
    private val server = MockWebServer()
    private val dispatcher = MockDispatcher()
    override val baseUrl: String
        get() = server.url("").toString()
    override val interceptedRequests: List<NetMockRequest>
        get() = dispatcher.interceptedRequests
    override val allowedMocks: List<NetMockRequestResponse>
        get() = dispatcher.requestResponseList

    init {
        server.dispatcher = dispatcher
    }

    override fun start() {
        server.start()
    }

    override fun addMock(
        request: NetMockRequest,
        response: NetMockResponse
    ) {
        dispatcher.requestResponseList.add(NetMockRequestResponse(request, response))
    }

    override fun setDefaultResponse(netMockResponse: NetMockResponse) {
        dispatcher.defaultResponse = netMockResponse
    }

    override fun shutDown() {
        server.shutdown()
    }

}