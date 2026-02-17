package com.denisbrandi.netmock.server.mappers

import com.denisbrandi.netmock.NetMockResponse
import mockwebserver3.MockResponse
import okhttp3.Headers.Companion.toHeaders

internal object MockWebServerResponseMapper {
    fun mapResponse(netMockResponse: NetMockResponse): MockResponse {
        return MockResponse(
            code = netMockResponse.code,
            headers = netMockResponse.mandatoryHeaders.toHeaders(),
            body = netMockResponse.body
        )
    }
}
