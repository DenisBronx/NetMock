package com.denisbrandi.netmock.server.mappers

import com.denisbrandi.netmock.NetMockResponse
import okhttp3.Headers.Companion.toHeaders
import okhttp3.mockwebserver.MockResponse

internal object MockWebServerResponseMapper {
    fun mapResponse(netMockResponse: NetMockResponse): MockResponse {
        return MockResponse().setResponseCode(netMockResponse.code)
            .setHeaders(netMockResponse.mandatoryHeaders.toHeaders())
            .setBody(netMockResponse.body)
    }
}
