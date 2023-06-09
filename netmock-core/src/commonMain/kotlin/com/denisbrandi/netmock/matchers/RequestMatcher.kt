package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.InterceptedRequest
import com.denisbrandi.netmock.NetMockRequest

interface RequestMatcher {
    fun isMatchingTheRequest(interceptedRequest: InterceptedRequest, expectedRequest: NetMockRequest): Boolean
}
