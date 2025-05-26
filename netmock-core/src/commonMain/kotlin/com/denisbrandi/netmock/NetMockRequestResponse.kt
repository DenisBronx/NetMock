package com.denisbrandi.netmock

/**
 * Represents a triple containing a [NetMockRequest], a [NetMockResponse], and a [retainMock] flag.
 * This structure is used to define a request that needs to be intercepted, the corresponding response to be mocked,
 * and whether the mock should persist after the first interception.
 *
 * @property request The request that needs to be intercepted and matched against incoming HTTP requests.
 * @property response The response to be returned when the [request] is successfully intercepted.
 * @property retainMock If `true`, the triple will remain in the queue after the first interception, allowing it
 * to be reused for subsequent matching requests. If `false`, the triple will be removed after the first match.
 * Defaults to `false`.
 */
data class NetMockRequestResponse(
    val request: NetMockRequest,
    val response: NetMockResponse,
    val retainMock: Boolean = false
)
