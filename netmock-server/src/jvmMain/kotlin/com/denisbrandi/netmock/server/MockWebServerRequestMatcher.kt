package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.matchers.RequestBodyMatcher
import com.denisbrandi.netmock.matchers.RequestMatcher
import com.denisbrandi.netmock.matchers.RequestPathMatcher
import okhttp3.mockwebserver.RecordedRequest

internal object MockWebServerRequestMatcher : RequestMatcher<RecordedRequest> {
    override fun isMatchingTheRequest(
        recordedRequest: RecordedRequest,
        recordedRequestBody: String,
        expectedRequest: NetMockRequest
    ): Boolean {
        return recordedRequest.method == expectedRequest.method.name &&
            RequestPathMatcher.isMatchingPathAndParams(recordedRequest.path, expectedRequest) &&
            RequestBodyMatcher.isMatchingTheBody(recordedRequestBody, expectedRequest.body) &&
            isMatchingTheHeaders(recordedRequest, expectedRequest)
    }

    private fun isMatchingTheHeaders(
        recordedRequest: RecordedRequest,
        netMockRequest: NetMockRequest
    ): Boolean {
        for (header in netMockRequest.containsHeaders) {
            if (recordedRequest.headers[header.key] != header.value) {
                return false
            }
        }
        return true
    }
}
