package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.engine.KtorResponseMapper.mapResponse
import com.denisbrandi.netmock.interceptors.RequestInterceptor
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.content.*
import java.util.logging.Logger

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
        return intercept(request, requestBody) ?: returnDefaultErrorResponseAndLogError(request, requestBody)
    }

    private fun returnDefaultErrorResponseAndLogError(
        request: HttpRequestData,
        recordedRequestBody: String
    ): HttpResponseData {
        val errorMessage =
            "Request not mocked:\n${request}\nWith headers:\n${request.headers}With body:\n${recordedRequestBody}"
        Logger.getLogger("NetMock").apply {
            severe(errorMessage)
            info("The following requests and responses were expected:\n${allowedMocks}")
            info("The following requests have been successfully mocked:\n${interceptedRequests}")
        }

        return mapResponse(NetMockResponse(code = 400, body = errorMessage))
    }
}