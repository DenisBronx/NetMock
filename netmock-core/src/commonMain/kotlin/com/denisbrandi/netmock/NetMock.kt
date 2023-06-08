package com.denisbrandi.netmock

/**
 * NetMock
 *
 * @property [baseUrl]: generated base url that needs to be used as base url for your production code requests.
 * @property [interceptedRequests]: list of the mocked requests that have been successfully intercepted.
 * @property [allowedMocks]: list of the mocked requests/responses that have not been intercepted yet.
 * @property [defaultResponse]: response returned for requests that do not match the [allowedMocks], when `null` a 400 Bad Request response is returned.
 */
interface NetMock {
    val baseUrl: String
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>
    var defaultResponse: NetMockResponse?

    /**
     * When a real request matches the provided [request], the provided [response] is returned.
     * Once a real request is successfully intercepted the provided [request] and [response] are removed from the queue.
     * If you want to make multiple identical requests and return the same response n times, just call this method n times with the same [request] and [response].
     * @param [request] The request that has to match with the intercepted HTTP request
     * @param [response] The response that will be mapped to HTTP response if the request matched an intercepted HTTP request.
     */
    fun addMock(request: NetMockRequest, response: NetMockResponse)

    /**
     * See [NetMock.addMock]
     * Alternative way of adding request/response to the queue using builders.
     *
     * @param [request] Function that allows you to create a [NetMockRequest] using a builder.
     * @param [response] Function that allows you to create a [NetMockResponse] using a builder.
     */
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
