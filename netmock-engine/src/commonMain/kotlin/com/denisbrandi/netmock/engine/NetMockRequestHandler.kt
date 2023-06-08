package com.denisbrandi.netmock.engine

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.engine.KtorResponseMapper.mapResponse
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
        return intercept(request, requestBody) ?: returnDefaultErrorResponseAndLogError(request, requestBody)
    }

    private fun returnDefaultErrorResponseAndLogError(
        request: HttpRequestData,
        recordedRequestBody: String
    ): HttpResponseData {
        val errorMessage =
            "\n----\nRequest not mocked:\n${request}\nWith headers:\n${request.headers}With body:\n${recordedRequestBody}" +
                    "\n\nThe following requests and responses were expected:\n${allowedMocks}" +
                    "\n\nThe following requests have been successfully mocked:\n${interceptedRequests}" +
                    "\n----"
        logError(errorMessage)
        return mapResponse(NetMockResponse(code = 400, body = errorMessage))
    }

    private fun logError(errorMessage: String) {
        val logger = Logger(loggerConfigInit(CommonWriter()))
        logger.e(messageString = errorMessage, tag = "NetMock")
    }
}