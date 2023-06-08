package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.NetMock
import com.denisbrandi.netmock.interceptors.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*

/**
 * Wrapper of [MockEngine], used to intercept Ktor requests and responses.
 * Example:
    private val netMock = NetMockEngine()
    private val httpClient = HttpClient(netMock)
 */
class NetMockEngine private constructor(
    mockEngine: MockEngine,
    private val mockRequestHandler: NetMockRequestHandler
) : NetMock,
    RequestInterceptor<HttpRequestData, HttpResponseData> by mockRequestHandler,
    HttpClientEngine by mockEngine {

    override val baseUrl: String
        get() = "http://localhost:58259/"

    companion object {
        operator fun invoke(config: MockEngineConfig = MockEngineConfig()): NetMockEngine {
            val netMockRequestHandler = NetMockRequestHandler(
                RequestInterceptorImpl(KtorRequestMatcher, KtorResponseMapper)
            )
            config.requestHandlers.add(netMockRequestHandler)
            return NetMockEngine(
                MockEngine(config),
                netMockRequestHandler
            )
        }
    }
}