package com.denisbrandi.netmock.engine.mappers

import com.denisbrandi.netmock.interceptors.InterceptedRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.http.Headers
import io.ktor.http.content.OutgoingContent

internal object KtorRequestMapper {
    fun mapRequest(request: HttpRequestData): InterceptedRequest {
        return InterceptedRequest(
            requestUrl = request.url.toString(),
            method = request.method.value,
            headers = mapHeaders(request.headers),
            body = mapBody(request.body)
        )
    }

    private fun mapHeaders(headers: Headers): Map<String, String> {
        return buildMap {
            headers.entries().forEach {
                put(it.key, it.value.first())
            }
        }
    }

    private fun mapBody(outgoingContent: OutgoingContent): String {
        return if (outgoingContent is OutgoingContent.ByteArrayContent) {
            outgoingContent.bytes().decodeToString()
        } else {
            ""
        }
    }
}
