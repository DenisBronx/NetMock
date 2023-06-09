package com.denisbrandi.netmock.engine

import com.denisbrandi.netmock.NetMockRequest
import com.denisbrandi.netmock.matchers.RequestBodyMatcher
import com.denisbrandi.netmock.matchers.RequestMatcher
import com.denisbrandi.netmock.matchers.RequestUrlMatcher
import io.ktor.client.request.*
import io.ktor.http.*

internal object KtorRequestMatcher : RequestMatcher<HttpRequestData> {
    override fun isMatchingTheRequest(
        recordedRequest: HttpRequestData,
        recordedRequestBody: String,
        expectedRequest: NetMockRequest
    ): Boolean {
        return recordedRequest.method.value == expectedRequest.method.name &&
            RequestUrlMatcher.isMatchingUrl(
                recordedRequest.url.toString(),
                expectedRequest
            ) &&
            RequestBodyMatcher.isMatchingTheBody(recordedRequestBody, expectedRequest.body) &&
            isMatchingTheHeaders(recordedRequest, expectedRequest)
    }

    private fun isMatchingTheHeaders(
        recordedRequest: HttpRequestData,
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
