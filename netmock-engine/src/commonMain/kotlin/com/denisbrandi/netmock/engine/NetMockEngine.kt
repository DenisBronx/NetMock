package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.NetMock
import com.denisbrandi.netmock.interceptors.DefaultInterceptor
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import com.denisbrandi.netmock.matchers.DefaultRequestMatcher
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig

/**
 * Wrapper of [MockEngine], used to intercept Ktor requests and responses.
 * Example:
 * ```
 * private val netMock = NetMockEngine()
 * private val httpClient = HttpClient(netMock)
 * ```
 */
class NetMockEngine private constructor(
    mockEngine: MockEngine,
    private val mockRequestHandler: NetMockRequestHandler
) : NetMock, RequestInterceptor by mockRequestHandler, HttpClientEngine by mockEngine {

    companion object {
        operator fun invoke(config: MockEngineConfig = MockEngineConfig()): NetMockEngine {
            val netMockRequestHandler = NetMockRequestHandler(
                DefaultInterceptor(DefaultRequestMatcher)
            )
            config.requestHandlers.add { netMockRequestHandler(it) }
            return NetMockEngine(
                MockEngine(config),
                netMockRequestHandler
            )
        }
    }
}
