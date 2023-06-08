package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.*
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

    override fun intercept(interceptedRequest: Request, interceptedRequestBody: String): Response? {
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
        return matchedResponse ?: defaultResponse?.let { responseMapper.mapResponse(it) }
    }
}
