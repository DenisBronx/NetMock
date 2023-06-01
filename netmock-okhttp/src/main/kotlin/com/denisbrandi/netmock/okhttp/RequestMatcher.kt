package com.denisbrandi.netmock.okhttp

import com.denisbrandi.netmock.NetMockRequest
import kotlinx.serialization.json.*
import okhttp3.mockwebserver.RecordedRequest

internal object RequestMatcher {
    fun isMatchingTheRequest(
        recordedRequest: RecordedRequest,
        recordedRequestBody: String,
        netMockRequest: NetMockRequest
    ): Boolean {
        return recordedRequest.method == netMockRequest.method.name &&
                isMatchingPathAndParams(recordedRequest, netMockRequest) &&
                isMatchingTheBody(recordedRequestBody, netMockRequest.body) &&
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

    private fun isMatchingTheBody(recordedRequestBody: String, netMockRequestBody: String): Boolean {
        return if (recordedRequestBody == netMockRequestBody) {
            true
        } else {
            val recordedJsonObject = asJsonObject(recordedRequestBody)
            if (recordedJsonObject != null) {
                recordedJsonObject == asJsonObject(netMockRequestBody)
            } else {
                val recordedJsonArray = asJsonArray(recordedRequestBody)
                if (recordedJsonArray != null) {
                    recordedJsonArray == asJsonArray(netMockRequestBody)
                } else {
                    false
                }
            }
        }
    }

    private fun asJsonObject(jsonString: String): JsonObject? {
        return try {
            Json.decodeFromString<JsonObject>(jsonString)
        } catch (t: Throwable) {
            null
        }
    }

    private fun asJsonArray(jsonString: String): JsonArray? {
        return try {
            Json.decodeFromString<JsonArray>(jsonString)
        } catch (t: Throwable) {
            null
        }
    }
}