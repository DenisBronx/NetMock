package com.denisbrandi.netmock.server

import com.denisbrandi.netmock.NetMockResponse
import com.denisbrandi.netmock.mappers.ResponseMapper
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.MockResponse

internal object MockWebServerResponseMapper: ResponseMapper<MockResponse> {
    override fun mapResponse(netMockResponse: NetMockResponse): MockResponse {
        return MockResponse().setResponseCode(netMockResponse.code)
            .setHeaders(netMockResponse.containsHeaders.toHeaders())
            .setBody(netMockResponse.body)
    }
}