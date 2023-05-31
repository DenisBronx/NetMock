package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.NetMockResponse
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.MockResponse

internal object ResponseMapper {
    fun mapResponse(netMockResponse: NetMockResponse): MockResponse {
        return MockResponse().setResponseCode(netMockResponse.code)
            .setHeaders(netMockResponse.containsHeaders.toHeaders())
            .setBody(netMockResponse.body)
    }
}