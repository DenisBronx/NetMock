package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.*

interface RequestInterceptor<Request : Any, Response : Any> {
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>
    var defaultResponse: NetMockResponse?

    fun addMock(request: NetMockRequest, response: NetMockResponse)

    fun intercept(interceptedRequest: Request, interceptedRequestBody: String): Response?
}