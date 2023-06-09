package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.engine.mappers.KtorRequestMapper
import com.denisbrandi.netmock.engine.mappers.KtorResponseMapper
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*

internal class NetMockRequestHandler(
    ktorInterceptor: RequestInterceptor
) : suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData, RequestInterceptor by ktorInterceptor {

    override suspend fun invoke(scope: MockRequestHandleScope, request: HttpRequestData): HttpResponseData {
        val netMockResponse = intercept(KtorRequestMapper.mapRequest(request))
        return KtorResponseMapper.mapResponse(netMockResponse)
    }
}
