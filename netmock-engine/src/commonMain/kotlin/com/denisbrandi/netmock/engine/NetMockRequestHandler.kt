package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.engine.mappers.KtorRequestMapper
import com.denisbrandi.netmock.engine.mappers.KtorResponseMapper
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData

internal class NetMockRequestHandler(
    ktorInterceptor: RequestInterceptor
) : RequestInterceptor by ktorInterceptor {

    operator fun invoke(request: HttpRequestData): HttpResponseData {
        val netMockResponse = intercept(KtorRequestMapper.mapRequest(request))
        return KtorResponseMapper.mapResponse(netMockResponse)
    }
}
