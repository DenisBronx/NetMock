package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.*

internal class MockDispatcher: Dispatcher() {

    val requestResponseList = mutableListOf<NetMockRequestResponse>()
    val interceptedRequests = mutableListOf<NetMockRequest>()

    var defaultResponse: NetMockResponse? = null

    fun addMapping(netMockRequest: NetMockRequest, netMockResponse: NetMockResponse) {
        requestResponseList.add(NetMockRequestResponse(netMockRequest, netMockResponse))
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return matchRequest(request) ?: getDefaultResponse(request)
    }

    private fun getDefaultResponse(request: RecordedRequest): MockResponse {
        return defaultResponse?.let { mapResponse(it) } ?: mapResponse(NetMockResponse(code = 400, body = "Request not mocked:\n${request}"))
    }

    private fun matchRequest(recordedRequest: RecordedRequest): MockResponse? {
        return requestResponseList.filter { requestResponse ->
            isMatchingTheRequest(recordedRequest, requestResponse.request)
        }.firstNotNullOfOrNull { requestResponse ->
            interceptedRequests.add(requestResponse.request)
            requestResponseList.remove(requestResponse)
            mapResponse(requestResponse.response)
        }
    }

    private fun isMatchingTheRequest(recordedRequest: RecordedRequest, netMockRequest: NetMockRequest): Boolean {
        return recordedRequest.method == netMockRequest.method.name &&
                isMatchingPathAndParams(recordedRequest, netMockRequest) &&
                recordedRequest.body.readUtf8() == netMockRequest.body &&
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

    private fun mapResponse(netMockResponse: NetMockResponse): MockResponse {
        return MockResponse().setResponseCode(netMockResponse.code)
            .setHeaders(netMockResponse.containsHeaders.toHeaders())
            .setBody(netMockResponse.body)
    }
}