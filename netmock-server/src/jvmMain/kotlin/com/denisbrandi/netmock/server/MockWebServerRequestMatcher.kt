package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.matchers.RequestBodyMatcher
import com.denisbrandi.netmock.matchers.RequestMatcher
import com.denisbrandi.netmock.matchers.RequestUrlMatcher
import okhttp3.mockwebserver.RecordedRequest

internal object MockWebServerRequestMatcher : RequestMatcher<RecordedRequest> {

    const val INTERCEPTED_REQUEST_URL_HEADER = "NET_MOCK_RESERVED_HEADER/interceptedRequestUrl"
    override fun isMatchingTheRequest(
        recordedRequest: RecordedRequest,
        recordedRequestBody: String,
        expectedRequest: NetMockRequest
    ): Boolean {
        val requestUrl = if (recordedRequest.headers[INTERCEPTED_REQUEST_URL_HEADER] != null) {
            recordedRequest.headers[INTERCEPTED_REQUEST_URL_HEADER]
        } else {
            recordedRequest.requestUrl?.toString()
        }
        return recordedRequest.method == expectedRequest.method.name &&
            RequestUrlMatcher.isMatchingUrl(requestUrl, expectedRequest.requestUrl) &&
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
