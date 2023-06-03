package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMock
import com.denisbrandi.netmock.interceptors.*
import okhttp3.mockwebserver.*

class NetMockServer private constructor(
    private val dispatcher: MockDispatcher
) : NetMock, RequestInterceptor<RecordedRequest, MockResponse> by dispatcher {
    private val server = MockWebServer()
    override val baseUrl: String
        get() = server.url("").toString()

    init {
        server.dispatcher = dispatcher
    }

    fun start() {
        server.start()
    }

    fun shutDown() {
        server.shutdown()
    }

    companion object {
        operator fun invoke(): NetMockServer {
            return NetMockServer(
                MockDispatcher(RequestInterceptorImpl(MockWebServerRequestMatcher, MockWebServerResponseMapper))
            )
        }
    }

}