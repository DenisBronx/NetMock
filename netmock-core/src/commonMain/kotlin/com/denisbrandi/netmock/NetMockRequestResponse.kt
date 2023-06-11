package com.denisbrandi.netmock

/**
 * Pair of a [NetMockRequest] and a [NetMockResponse]
 *
 * @property request
 * @property response
 */
data class NetMockRequestResponse(val request: NetMockRequest, val response: NetMockResponse)
