package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.interceptors.InterceptedRequest

interface RequestMatcher {
    fun isMatchingTheRequest(interceptedRequest: InterceptedRequest, expectedRequest: NetMockRequest): Boolean
}
