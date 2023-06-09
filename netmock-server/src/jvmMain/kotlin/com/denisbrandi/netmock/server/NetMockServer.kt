package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMock
import com.denisbrandi.netmock.interceptors.DefaultInterceptor
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import com.denisbrandi.netmock.matchers.DefaultRequestMatcher
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockWebServer

/**
 * Wrapper of [MockWebServer], used to intercept requests and responses directed to localhost.
 * In order to use [NetMock.addMock] you'll need to start the server first in order to obtain a [NetMockServer.baseUrl].
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
) : NetMock, RequestInterceptor by dispatcher {
    private val server = MockWebServer()
    val baseUrl: String
        get() = server.url("").toString()

    val interceptor: Interceptor = Interceptor { chain -> NetMockChainRequestInterceptor.intercept(chain, baseUrl) }

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
                    DefaultInterceptor(DefaultRequestMatcher)
                )
            )
        }
    }
}
