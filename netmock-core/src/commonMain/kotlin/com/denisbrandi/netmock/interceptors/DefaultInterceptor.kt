package com.denisbrandi.netmock.interceptors

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockRequestResponse
import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.matchers.RequestMatcher

class DefaultInterceptor(
    private val requestMatcher: RequestMatcher
) : RequestInterceptor {
    override val allowedMocks = mutableListOf<NetMockRequestResponse>()
    override val interceptedRequests = mutableListOf<NetMockRequest>()

    override var defaultResponse: NetMockResponse? = null

    override fun addMock(request: NetMockRequest, response: NetMockResponse) {
        allowedMocks.add(NetMockRequestResponse(request, response))
    }

    override fun intercept(interceptedRequest: InterceptedRequest): NetMockResponse {
        val matchedResponse = allowedMocks.filter { requestResponse ->
            requestMatcher.isMatchingTheRequest(interceptedRequest, requestResponse.request)
        }.firstNotNullOfOrNull { requestResponse ->
            interceptedRequests.add(requestResponse.request)
            allowedMocks.remove(requestResponse)
            requestResponse.response
        }
        return matchedResponse ?: defaultResponse
            ?: returnDefaultErrorResponseAndLogError(interceptedRequest)
    }

    private fun returnDefaultErrorResponseAndLogError(request: InterceptedRequest): NetMockResponse {
        val errorMessage =
            "\n----\nRequest not mocked:\n${request.method} ${request.requestUrl}\nWith headers:\n${request.headers}\nWith body:\n${request.body}" +
                "\n\nThe following requests and responses were expected:\n$allowedMocks" +
                "\n\nThe following requests have been successfully mocked:\n$interceptedRequests" +
                "\n----"
        logError(errorMessage)
        return NetMockResponse(code = 400, body = errorMessage)
    }

    private fun logError(errorMessage: String) {
        val logger = Logger(loggerConfigInit(CommonWriter()))
        logger.e(messageString = errorMessage, tag = "NetMock")
    }
}
