package com.denisbrandi.netmock

/**
 * NetMock
 *
 * @property [interceptedRequests]: list of the mocked requests that have been successfully intercepted.
 * @property [allowedMocks]: list of the mocked requests/responses that have not been intercepted yet, mocks with custom matchers excluded.
 * @property [defaultResponse]: response returned for requests that do not match the [allowedMocks], when `null` a 400 Bad Request response is returned.
 */
interface NetMock {
    val interceptedRequests: List<NetMockRequest>
    val allowedMocks: List<NetMockRequestResponse>
    var defaultResponse: NetMockResponse?

    /**
     * Mocks a response for a specific request. When an intercepted HTTP request matches the provided [request],
     * the specified [response] is returned. After a successful interception, the [request] and [response] pair
     * is removed from the queue unless [retainMock] is set to `true`.
     *
     * To mock multiple identical requests with the same response, call this method multiple times with the same
     * [request] and [response]. Alternatively, set [retainMock] to `true` to keep the [request]/[response] pair
     * in the queue indefinitely, ensuring all matching requests are mocked.
     *
     * By default, [retainMock] is set to `false`, meaning the mock is removed after the first match.
     *
     * @param request The request criteria to match against intercepted HTTP requests.
     * @param response The response to return when the request criteria are met.
     * @param retainMock If `true`, the mock remains in the queue after the first match, allowing it to be reused
     * for subsequent requests. If `false`, the mock is removed after the first match. Defaults to `false`.
     */
    fun addMock(request: NetMockRequest, response: NetMockResponse, retainMock: Boolean = false)

    /**
     * An alternative way to add a [request] and [response] pair to the queue using a builder pattern.
     * This method functions similarly to [NetMock.addMock], but provides a more flexible and readable approach
     * for defining responses using a builder.
     *
     * When an intercepted HTTP request matches the provided [request], the response created by the [response]
     * builder is returned. By default, the mock is removed after the first match unless [retainMock] is set to `true`.
     *
     * @param request The request criteria to match against intercepted HTTP requests.
     * @param response A builder function that allows you to construct a [NetMockResponse] with custom properties.
     * @param retainMock If `true`, the mock remains in the queue after the first match, allowing it to be reused
     * for subsequent requests. If `false`, the mock is removed after the first match. Defaults to `false`.
     *
     * @see [NetMock.addMock] for the standard method of adding request/response pairs.
     */
    fun addMock(
        request: NetMockRequest,
        response: NetMockResponseBuilder.() -> Unit,
        retainMock: Boolean = false
    ) {
        val responseBuilder = NetMockResponseBuilder()
        response(responseBuilder)
        addMock(request, responseBuilder.build(), retainMock)
    }

    /**
     * An alternative way to add a [request] and [response] pair to the queue using builder patterns for both.
     * This method functions similarly to [NetMock.addMock], but provides a more flexible and readable approach
     * for defining both requests and responses using builders.
     *
     * The [request] builder allows you to construct a [NetMockRequest] with custom properties, while the [response]
     * builder enables you to create a [NetMockResponse] tailored to your needs. This method is ideal for scenarios
     * where you want to define complex request and response configurations in a clean and structured way.
     *
     * @param request A builder function that allows you to construct a [NetMockRequest] with custom properties.
     * @param response A builder function that allows you to construct a [NetMockResponse] with custom properties.
     * @param retainMock If `true`, the mock remains in the queue after the first match, allowing it to be reused
     * for subsequent requests. If `false`, the mock is removed after the first match. Defaults to `false`.
     *
     * @see [NetMock.addMock] for the standard method of adding request/response pairs.
     */
    fun addMock(
        request: NetMockRequestBuilder.() -> Unit,
        response: NetMockResponseBuilder.() -> Unit,
        retainMock: Boolean = false
    ) {
        val requestBuilder = NetMockRequestBuilder()
        request(requestBuilder)
        addMock(requestBuilder.build(), response, retainMock)
    }

    /**
     * Mocks a response for requests that match a custom criteria defined by [requestMatcher]. When an intercepted
     * HTTP request satisfies the [requestMatcher] condition, the specified [response] is returned. After a successful
     * interception, the [requestMatcher] and [response] pair is removed from the queue unless [retainMock] is set to `true`.
     *
     * To mock multiple identical requests with the same response, call this method multiple times with the same
     * [requestMatcher] and [response]. Alternatively, set [retainMock] to `true` to keep the [requestMatcher]/[response]
     * pair in the queue indefinitely, ensuring all matching requests are mocked.
     *
     * By default, [retainMock] is set to `false`, meaning the mock is removed after the first match.
     *
     * @param requestMatcher A custom function that defines the criteria for matching intercepted HTTP requests.
     * @param response The response to return when the request matches the [requestMatcher] criteria.
     * @param retainMock If `true`, the mock remains in the queue after the first match, allowing it to be reused
     * for subsequent requests. If `false`, the mock is removed after the first match. Defaults to `false`.
     */
    fun addMockWithCustomMatcher(
        requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
        response: NetMockResponse,
        retainMock: Boolean = false
    )

    /**
     * An alternative way to add a [requestMatcher] and [response] pair to the queue using a builder pattern.
     * This method functions similarly to [NetMock.addMockWithCustomMatcher], but provides a more flexible and
     * readable approach for defining responses using a builder.
     *
     * When an intercepted HTTP request matches the custom criteria defined by [requestMatcher], the response
     * created by the [response] builder is returned. By default, the mock is removed after the first match unless
     * [retainMock] is set to `true`.
     *
     * @param requestMatcher A custom function that defines the criteria for matching intercepted HTTP requests.
     * @param response A builder function that allows you to construct a [NetMockResponse] with custom properties.
     * @param retainMock If `true`, the mock remains in the queue after the first match, allowing it to be reused
     * for subsequent requests. If `false`, the mock is removed after the first match. Defaults to `false`.
     *
     * @see [NetMock.addMockWithCustomMatcher] for the standard method of adding custom matchers.
     */
    fun addMockWithCustomMatcher(
        requestMatcher: (interceptedRequest: NetMockRequest) -> Boolean,
        response: NetMockResponseBuilder.() -> Unit,
        retainMock: Boolean = false
    ) {
        val responseBuilder = NetMockResponseBuilder()
        response(responseBuilder)
        addMockWithCustomMatcher(requestMatcher, responseBuilder.build(), retainMock)
    }
}
