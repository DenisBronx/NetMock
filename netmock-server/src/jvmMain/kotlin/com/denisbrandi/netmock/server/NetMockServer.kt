package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMock
import com.denisbrandi.netmock.interceptors.*
import okhttp3.mockwebserver.*

/**
 * Wrapper of [MockWebServer], used to intercept requests and responses directed to localhost.
 * In order to use [NetMock.addMock] you'll need to start the server first in order to obtain a [NetMock.baseUrl].
 * You should also stop the server at the end of each test.
 * Example:
 private val netMock = NetMockServer()
 @Before
 fun setUp() {
 netMock.start()
 }

 @After
 fun tearDown() {
 netMock.shutDown()
 }
 * To avoid to do this manually use [NetMockServerRule] which will deal with start and shutDown for you.
 */
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
                MockDispatcher(
                    RequestInterceptorImpl(MockWebServerRequestMatcher, MockWebServerResponseMapper)
                )
            )
        }
    }
}
