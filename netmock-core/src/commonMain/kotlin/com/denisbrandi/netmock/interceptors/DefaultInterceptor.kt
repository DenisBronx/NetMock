package com.denisbrandi.netmock.interceptors

import co.touchlab.kermit.*
import com.denisbrandi.netmock.*
import com.denisbrandi.netmock.matchers.RequestMatcher

class DefaultInterceptor(
    private val requestMatcher: RequestMatcher
) : RequestInterceptor {
    override val allowedMocks = mutableListOf<NetMockRequestResponse>()
    override val interceptedRequests = mutableListOf<NetMockRequest>()
    private val customInterceptors = mutableListOf<CustomInterceptor>()

    override var defaultResponse: NetMockResponse? = null

    override fun addMock(request: NetMockRequest, response: NetMockResponse) {
        allowedMocks.add(NetMockRequestResponse(request, response))
    }

    override fun addMockWithCustomMatcher(
        requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
        response: NetMockResponse
    ) {
        customInterceptors.add(CustomInterceptor(requestMatcher, response))
    }

    override fun intercept(interceptedRequest: InterceptedRequest): NetMockResponse {
        return getMatchedResponseFromCustomInterceptors(interceptedRequest)
            ?: getMatchedResponseFromMocks(interceptedRequest)
            ?: defaultResponse
            ?: returnDefaultErrorResponseAndLogError(interceptedRequest)
    }

    private fun getMatchedResponseFromCustomInterceptors(interceptedRequest: InterceptedRequest): NetMockResponse? {
        val interceptedNetMockRequest = interceptedRequest.toNetMockRequest()
        return customInterceptors.firstOrNull { customInterceptor ->
            customInterceptor.requestMatcher(interceptedNetMockRequest)
        }?.let { customInterceptor ->
            interceptedRequests.add(interceptedNetMockRequest)
            customInterceptors.remove(customInterceptor)
            customInterceptor.response
        }
    }

    private fun InterceptedRequest.toNetMockRequest(): NetMockRequest {
        return NetMockRequest(
            requestUrl = requestUrl,
            method = Method.from(method),
            mandatoryHeaders = headers,
            body = body
        )
    }

    private fun getMatchedResponseFromMocks(interceptedRequest: InterceptedRequest): NetMockResponse? {
        return allowedMocks.firstOrNull { requestResponse ->
            requestMatcher.isMatchingTheRequest(interceptedRequest, requestResponse.request)
        }?.let { requestResponse ->
            interceptedRequests.add(requestResponse.request)
            allowedMocks.remove(requestResponse)
            requestResponse.response
        }
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

    private class CustomInterceptor(
        val requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
        val response: NetMockResponse
    )
}
