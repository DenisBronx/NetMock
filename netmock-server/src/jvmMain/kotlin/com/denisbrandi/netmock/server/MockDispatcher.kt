package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.interceptors.RequestInterceptor
import com.denisbrandi.netmock.server.mappers.*
import mockwebserver3.*

internal class MockDispatcher(
    okHttpMockInterceptor: RequestInterceptor
) : Dispatcher(), RequestInterceptor by okHttpMockInterceptor {

    override fun dispatch(request: RecordedRequest): MockResponse {
        val netMockResponse = intercept(MockWebServerRequestMapper.mapRequest(request))
        return MockWebServerResponseMapper.mapResponse(netMockResponse)
    }
}

internal const val INTERCEPTED_REQUEST_URL_HEADER = "NET_MOCK_RESERVED_HEADER/interceptedRequestUrl"
