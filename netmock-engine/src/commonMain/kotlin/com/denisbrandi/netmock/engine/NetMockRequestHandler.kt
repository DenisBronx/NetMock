package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.interceptors.RequestInterceptor
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.content.*

internal class NetMockRequestHandler(
    ktorInterceptor: RequestInterceptor<HttpRequestData, HttpResponseData>
) : suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    RequestInterceptor<HttpRequestData, HttpResponseData> by ktorInterceptor {

    override suspend fun invoke(scope: MockRequestHandleScope, request: HttpRequestData): HttpResponseData {
        val body = request.body
        val requestBody = if (body is TextContent) {
            body.text
        } else {
            ""
        }
        return intercept(request, request.headers, requestBody)
    }
}
