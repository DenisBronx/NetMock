package com.denisbrandi.netmock.server.mappers

import com.denisbrandi.netmock.interceptors.InterceptedRequest
import com.denisbrandi.netmock.server.INTERCEPTED_REQUEST_URL_HEADER
import mockwebserver3.RecordedRequest

internal object MockWebServerRequestMapper {
    fun mapRequest(request: RecordedRequest): InterceptedRequest {
        return InterceptedRequest(
            requestUrl = request.headers[INTERCEPTED_REQUEST_URL_HEADER] ?: request.url.toString(),
            method = request.method,
            headers = request.headers.toMap(),
            body = request.body?.utf8().orEmpty()
        )
    }
}
