package com.denisbrandi.netmock.interceptors

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockRequestResponse
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.mappers.ResponseMapper
import com.denisbrandi.netmock.matchers.RequestMatcher

class RequestInterceptorImpl<Request : Any, Response : Any>(
    private val requestMatcher: RequestMatcher<Request>,
    private val responseMapper: ResponseMapper<Response>
) : RequestInterceptor<Request, Response> {
    override val allowedMocks = mutableListOf<NetMockRequestResponse>()
    override val interceptedRequests = mutableListOf<NetMockRequest>()

    override var defaultResponse: NetMockResponse? = null

    override fun addMock(request: NetMockRequest, response: NetMockResponse) {
        allowedMocks.add(NetMockRequestResponse(request, response))
    }

    override fun intercept(interceptedRequest: Request, headers: Any, interceptedRequestBody: String): Response {
        val matchedResponse = allowedMocks.filter { requestResponse ->
            requestMatcher.isMatchingTheRequest(
                interceptedRequest,
                interceptedRequestBody,
                requestResponse.request
            )
        }.firstNotNullOfOrNull { requestResponse ->
            interceptedRequests.add(requestResponse.request)
            allowedMocks.remove(requestResponse)
            responseMapper.mapResponse(requestResponse.response)
        }
        return matchedResponse
            ?: defaultResponse?.let { responseMapper.mapResponse(it) }
            ?: returnDefaultErrorResponseAndLogError(interceptedRequest, headers, interceptedRequestBody)
    }

    private fun returnDefaultErrorResponseAndLogError(
        request: Request,
        headers: Any,
        recordedRequestBody: String
    ): Response {
        val errorMessage =
            "\n----\nRequest not mocked:\n${request}\nWith headers:\n${headers}With body:\n$recordedRequestBody" +
                "\n\nThe following requests and responses were expected:\n$allowedMocks" +
                "\n\nThe following requests have been successfully mocked:\n$interceptedRequests" +
                "\n----"
        logError(errorMessage)
        return responseMapper.mapResponse(NetMockResponse(code = 400, body = errorMessage))
    }

    private fun logError(errorMessage: String) {
        val logger = Logger(loggerConfigInit(CommonWriter()))
        logger.e(messageString = errorMessage, tag = "NetMock")
    }
}
