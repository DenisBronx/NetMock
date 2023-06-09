package com.denisbrandi.netmock.server.mappers

import com.denisbrandi.netmock.interceptors.InterceptedRequest
import com.denisbrandi.netmock.server.INTERCEPTED_REQUEST_URL_HEADER
import okhttp3.mockwebserver.RecordedRequest

internal object MockWebServerRequestMapper {
    fun mapRequest(request: RecordedRequest): InterceptedRequest {
        val requestUrl = if (request.headers[INTERCEPTED_REQUEST_URL_HEADER] != null) {
            request.headers[INTERCEPTED_REQUEST_URL_HEADER]
        } else {
            request.requestUrl?.toString()
        }
        // Body can be read only once
        val recordedRequestBody = request.body.readUtf8()
        return InterceptedRequest(
            requestUrl = requestUrl,
            method = request.method,
            headers = request.headers.toMap(),
            body = recordedRequestBody
        )
    }
}
