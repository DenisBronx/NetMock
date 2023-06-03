package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.NetMockRequest

interface RequestMatcher<Request : Any> {
    fun isMatchingTheRequest(
        recordedRequest: Request,
        recordedRequestBody: String,
        expectedRequest: NetMockRequest
    ): Boolean
}