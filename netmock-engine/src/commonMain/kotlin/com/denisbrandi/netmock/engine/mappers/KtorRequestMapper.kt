package com.denisbrandi.netmock.engine.mappers

import com.denisbrandi.netmock.InterceptedRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*

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
            headers.forEach { key, _ ->
                put(key, headers[key].orEmpty())
            }
        }
    }

    private fun mapBody(outgoingContent: OutgoingContent): String {
        return if (outgoingContent is TextContent) {
            outgoingContent.text
        } else {
            ""
        }
    }
}
