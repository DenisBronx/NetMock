package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.*

interface RequestInterceptor {
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>
    var defaultResponse: NetMockResponse?

    fun addMock(request: NetMockRequest, response: NetMockResponse, retainMock: Boolean)

    fun addMockWithCustomMatcher(
        requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
        response: NetMockResponse,
        retainMock: Boolean
    )

    fun intercept(interceptedRequest: InterceptedRequest): NetMockResponse
}
