package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.engine.mappers.*
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import io.ktor.client.request.*

internal class NetMockRequestHandler(
    ktorInterceptor: RequestInterceptor
) : RequestInterceptor by ktorInterceptor {

    operator fun invoke(request: HttpRequestData): HttpResponseData {
        val netMockResponse = intercept(KtorRequestMapper.mapRequest(request))
        return KtorResponseMapper.mapResponse(netMockResponse)
    }
}
