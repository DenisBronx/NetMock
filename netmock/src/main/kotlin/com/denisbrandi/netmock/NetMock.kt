package com.denisbrandi.netmock

interface NetMock {
    val baseUrl: String
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>
    var defaultResponse: NetMockResponse?

    fun addMock(request: NetMockRequest, response: NetMockResponse)

    fun addMock(
        request: NetMockRequestBuilder.() -> Unit,
        response: NetMockResponseBuilder.() -> Unit
    ) {
        val requestBuilder = NetMockRequestBuilder()
        val responseBuilder = NetMockResponseBuilder()
        request(requestBuilder)
        response(responseBuilder)
        addMock(requestBuilder.build(), responseBuilder.build())
    }
}