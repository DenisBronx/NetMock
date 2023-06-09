package com.denisbrandi.netmock.matchers

import com.denisbrandi.netmock.InterceptedRequest
import com.denisbrandi.netmock.NetMockRequest

object DefaultRequestMatcher : RequestMatcher {
    override fun isMatchingTheRequest(interceptedRequest: InterceptedRequest, expectedRequest: NetMockRequest): Boolean {
        return interceptedRequest.method == expectedRequest.method.name &&
            RequestUrlMatcher.isMatchingUrl(interceptedRequest.requestUrl, expectedRequest.requestUrl) &&
            RequestHeadersMatcher.isMatchingTheHeaders(interceptedRequest.headers, expectedRequest.containsHeaders) &&
            RequestBodyMatcher.isMatchingTheBody(interceptedRequest.body, expectedRequest.body)
    }
}
