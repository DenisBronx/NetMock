package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.matchers.RequestBodyMatcher
import okhttp3.mockwebserver.RecordedRequest

internal object RequestMatcher {
    fun isMatchingTheRequest(
        recordedRequest: RecordedRequest,
        recordedRequestBody: String,
        netMockRequest: NetMockRequest
    ): Boolean {
        return recordedRequest.method == netMockRequest.method.name &&
                isMatchingPathAndParams(recordedRequest, netMockRequest) &&
                RequestBodyMatcher.isMatchingTheBody(recordedRequestBody, netMockRequest.body) &&
                isMatchingTheHeaders(recordedRequest, netMockRequest)
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