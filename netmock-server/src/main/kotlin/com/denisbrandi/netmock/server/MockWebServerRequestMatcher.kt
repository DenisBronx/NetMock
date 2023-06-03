package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.matchers.*
import okhttp3.mockwebserver.RecordedRequest

internal object MockWebServerRequestMatcher: RequestMatcher<RecordedRequest> {
    override fun isMatchingTheRequest(
        recordedRequest: RecordedRequest,
        recordedRequestBody: String,
        expectedRequest: NetMockRequest
    ): Boolean {
        return recordedRequest.method == expectedRequest.method.name &&
                isMatchingPathAndParams(recordedRequest, expectedRequest) &&
                RequestBodyMatcher.isMatchingTheBody(recordedRequestBody, expectedRequest.body) &&
                isMatchingTheHeaders(recordedRequest, expectedRequest)
    }

    private fun isMatchingPathAndParams(recordedRequest: RecordedRequest, netMockRequest: NetMockRequest): Boolean {
        var appendedParams = ""
        netMockRequest.params.forEach { (key, value) ->
            appendedParams += if (appendedParams.isEmpty()) {
                "?$key=$value"
            } else {
                "&$key=$value"
            }
        }
        val actualPath = "${netMockRequest.path}$appendedParams"
        return recordedRequest.path == actualPath
    }

    private fun isMatchingTheHeaders(recordedRequest: RecordedRequest, netMockRequest: NetMockRequest): Boolean {
        for (header in netMockRequest.containsHeaders) {
            if (recordedRequest.headers[header.key] != header.value) {
                return false
            }
        }
        return true
    }
}