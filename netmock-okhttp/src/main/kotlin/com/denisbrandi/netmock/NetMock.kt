package com.denisbrandi.netmock

interface NetMock {
    val baseUrl: String
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>

    fun addMock(request: NetMockRequest, response: NetMockResponse)

    fun setDefaultResponse(netMockResponse: NetMockResponse)

    fun shutDown()
}