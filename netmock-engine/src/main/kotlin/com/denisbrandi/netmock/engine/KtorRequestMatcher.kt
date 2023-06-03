package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.matchers.*
import io.ktor.client.request.*
import io.ktor.http.*

internal object KtorRequestMatcher: RequestMatcher<HttpRequestData> {
    override fun isMatchingTheRequest(
        recordedRequest: HttpRequestData,
        recordedRequestBody: String,
        expectedRequest: NetMockRequest
    ): Boolean {
        return recordedRequest.method.value == expectedRequest.method.name &&
                isMatchingPathAndParams(recordedRequest, expectedRequest) &&
                RequestBodyMatcher.isMatchingTheBody(recordedRequestBody, expectedRequest.body) &&
                isMatchingTheHeaders(recordedRequest, expectedRequest)
    }

    private fun isMatchingPathAndParams(recordedRequest: HttpRequestData, netMockRequest: NetMockRequest): Boolean {
        var appendedParams = ""
        netMockRequest.params.forEach { (key, value) ->
            appendedParams += if (appendedParams.isEmpty()) {
                "?$key=$value"
            } else {
                "&$key=$value"
            }
        }
        val actualPath = "${netMockRequest.path}$appendedParams"
        return recordedRequest.url.fullPath == actualPath
    }

    private fun isMatchingTheHeaders(recordedRequest: HttpRequestData, netMockRequest: NetMockRequest): Boolean {
        for (header in netMockRequest.containsHeaders) {
            if (recordedRequest.headers[header.key] != header.value) {
                return false
            }
        }
        return true
    }
}