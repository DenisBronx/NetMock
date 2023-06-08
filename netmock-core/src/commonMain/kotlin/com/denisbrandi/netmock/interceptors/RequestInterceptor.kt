package com.denisbrandi.netmock.interceptors

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.NetMockRequestResponse
import com.denisbrandi.netmock.NetMockResponse

interface RequestInterceptor<Request : Any, Response : Any> {
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>
    var defaultResponse: NetMockResponse?

    fun addMock(request: NetMockRequest, response: NetMockResponse)

    fun intercept(interceptedRequest: Request, headers: Any, interceptedRequestBody: String): Response
}
